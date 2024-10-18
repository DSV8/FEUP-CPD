package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable {
    private static final long HEARTBEAT_INTERVAL = 5000; // 5 seconds
    private static final int MAX_INPUT_ATTEMPTS = 5;

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private User user;
    private Player player;
    private boolean alive;
    private boolean inMatchmaking;
    private final Matchmaker matchmaker;
    private ScheduledExecutorService heartbeatExecutor;

    public ClientHandler(Socket clientSocket, Matchmaker matchmaker) {
        this.clientSocket = clientSocket;
        this.matchmaker = matchmaker;
        player = new Player();
        this.alive = true;
    }

    public User getUser() {
        return user;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isAlive () {
        return alive;
    }

    public boolean isInMatchmaking() {
        return inMatchmaking;
    }

    public void setInMatchmaking(boolean inMatchmaking) {
        this.inMatchmaking = inMatchmaking;
    }

    public void startHeartbeat() {
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().factory());
        heartbeatExecutor.scheduleAtFixedRate(this::sendHeartbeat, 0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void sendHeartbeat() {
        try {
            out.println("PING");
            clientSocket.setSoTimeout((int) HEARTBEAT_INTERVAL);
            String response = in.readLine();
            if (!"PONG".equals(response)) {
                handleDisconnection();
            }
        } catch (IOException e) {
            handleDisconnection();
        }
    }

    private void handleDisconnection() {
        alive = false;
        closeResources();
    }

    public void stopHeartbeat() {
        try {
            if (heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
                heartbeatExecutor.shutdown();
            }
            clientSocket.setSoTimeout(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String connectionType = in.readLine();

            if ("RECONNECTION".equals(connectionType)) {
                String token = in.readLine();
                matchmaker.restorePlayer(token, clientSocket, in, out);
                return;
            }

            // Prompt the client to choose between registration and authentication
            out.println("Choose an option:");
            out.println("1. Register");
            out.println("2. Login");

            String choice = null;
            for (int i = 0; i < MAX_INPUT_ATTEMPTS; i++) {
                choice = in.readLine();
                if ("1".equals(choice) || "2".equals(choice)) {
                    out.println("CHOICE_SUCCESS");
                    break;
                } else {
                    out.println("CHOICE_ERR Invalid choice. Try again.");
                    choice = null;
                }
            }

            if (choice == null) {
                out.println("Failed to pick an option. Disconnecting...");
                closeResources();
                return;
            }

            boolean authenticated = authenticate(in, out, choice);
            if (authenticated) {
                String token = UUID.randomUUID().toString();
                out.println(token);

                matchmaker.addPlayer(this, token);
                if (inMatchmaking) {
                    startHeartbeat(); // Start the heartbeat after adding to the matchmaker
                    out.println("Added to the Game Queue. Waiting for other players to join...");
                }
            } else {
                out.println("Authentication Failed. Disconnecting...");
                closeResources();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean authenticate(BufferedReader in, PrintWriter out, String option) throws IOException {
        boolean authenticated = false;
        for (int i = 0; i < MAX_INPUT_ATTEMPTS; i++) {
            out.println("Enter your username:");
            String username = in.readLine();
            if (!username.isEmpty() && !username.contains(" ")) {
                out.println("USERNAME_SUCCESS");
                for (int j = 0; j < MAX_INPUT_ATTEMPTS; j++) {
                    out.println("Enter your password:");
                    String password = in.readLine();
                    if (!password.isEmpty() && !password.contains(" ")) {
                        out.println("PASSWORD_SUCCESS");
                        if ("1".equals(option)) {
                            user = UserManager.register(username, password);
                            if (user != null) {
                                authenticated = true;
                                out.println("You have successfully registered!");
                            } else {
                                out.println("AUTH_ERR Username already in use!");
                            }
                        } else if ("2".equals(option)) {
                            user = UserManager.login(username, password);
                            if (user != null) {
                                authenticated = true;
                                out.println("You have successfully logged in!");
                            } else {
                                out.println("AUTH_ERR Invalid username or password");
                            }
                        }
                        break;
                    } else {
                        out.println("PASSWORD_ERR Password cannot contain whitespaces. Try again.");
                    }
                }
                break;
            } else {
                out.println("USERNAME_ERR Username cannot contain whitespaces. Try again.");
            }
        }

        return authenticated;
    }

    public Card playCard(Card.Suit suit) throws IOException {
        sendMessage("PLAY_CARD");

        List<Card> validCards = suit == null ? player.getHand() : player.getHand().stream().filter(card -> card.getSuit().equals(suit)).toList();

        if (validCards.isEmpty()) {
            return null;
        }

        sendMessage("Valid cards in your hand:");
        for (int i = 0; i < validCards.size(); i++) {
            sendMessage((i + 1) + ". " + validCards.get(i).getValue() + " of " + validCards.get(i).getSuit());
        }
        sendMessage("\n");

        sendMessage("PICK_CARD Write the number of the card you want to play:");

        int cardNumber;
        do {
            try {
                cardNumber = Integer.parseInt(in.readLine());
                System.out.println(cardNumber);
                if (cardNumber < 1 || cardNumber > validCards.size()) {
                    sendMessage("PICK_CARD_ERROR Invalid choice number. Please try again:");
                    continue;
                }
            } catch (NumberFormatException nfe) {
                sendMessage("PICK_CARD_ERROR Invalid input. Please enter a number:");
                continue;
            }
            break;
        } while (true);
        sendMessage("\n");

        Card chosenCard = validCards.get(cardNumber - 1);

        sendMessage("You picked " + chosenCard.getValue() + " of " + chosenCard.getSuit() + "\n");

        player.getHand().remove(chosenCard);

        sendMessage("PLAY_CARD_END");

        return chosenCard;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void resumeConnection(Socket newSocket, BufferedReader in, PrintWriter out) {
        this.clientSocket = newSocket;
        this.in = in;
        this.out = out;
        alive = true;
    }

    public void closeResources() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
                heartbeatExecutor.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
