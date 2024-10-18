package Client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class GameClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final int MAX_INPUT_ATTEMPTS = 5;
    private static final int RECONNECT_ATTEMPTS = 5;
    private static final long RECONNECT_DELAY_S = 2; // 2 seconds

    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    private static String token;
    //private final Object writeLock = new Object();

    public static void main(String[] args) {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            System.out.println("Connected to " + SERVER_ADDRESS + ":" + SERVER_PORT);

            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

            out.println("NEW_CONNECTION");

            // Prompt the user to choose between registration and authentication
            System.out.println(in.readLine()); // Prompt for choice
            System.out.println(in.readLine()); // Option 1: Register
            System.out.println(in.readLine()); // Option 2: Login

            boolean choiceAccepted = false;
            for (int i = 0; i < MAX_INPUT_ATTEMPTS; i++) {
                String choice = userIn.readLine();
                out.println(choice);
                String response = in.readLine();
                if (!response.startsWith("CHOICE_ERR")) {
                    choiceAccepted = true;
                    break;
                } else {
                    System.out.println(response.substring(11));
                }
            }

            if (!choiceAccepted) {
                System.out.println(in.readLine());
                closeResources();
                return;
            }

            // Authentication
            boolean authenticated = authenticate(in, out, userIn);
            if (authenticated) {
                token = in.readLine();

                handleServerMessages();
            } else {
                System.out.println(in.readLine());
                closeResources();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    private static boolean authenticate(BufferedReader in, PrintWriter out, BufferedReader userIn) throws IOException {
        boolean authenticated = false;
        String response;
        for (int i = 0; i < MAX_INPUT_ATTEMPTS; i++) {
            System.out.println(in.readLine()); // Enter username
            String username = userIn.readLine();
            out.println(username);
            if (!(response = in.readLine()).startsWith("USERNAME_ERR")) {
                for (int j = 0; j < MAX_INPUT_ATTEMPTS; j++) {
                    System.out.println(in.readLine()); // Enter password
                    String password = userIn.readLine();
                    out.println(password);
                    if (!(response = in.readLine()).startsWith("PASSWORD_ERR")) {
                        if (!(response = in.readLine()).startsWith("AUTH_ERR")) {
                            authenticated = true;
                            System.out.println(response);
                        } else {
                            System.out.println(response.substring(9));
                        }
                        break;
                    } else {
                        System.out.println(response.substring(13));
                    }
                }
                break;
            } else {
                System.out.println(response.substring(13));
            }
        }

        return authenticated;
    }

    private static void handleServerMessages() {
        boolean running = true;

        while (running) {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    // Acknowledges heartbeats while waiting in the queue
                    if ("PING".equals(message)) {
                        out.println("PONG");
                    } else if (message.equals("GAME_START")) {
                        playGame();
                    } else {
                        System.out.println(message);
                    }
                }
                // Server closed
                running = false;
            } catch (IOException e) {
                closeResources();
                System.err.println("Connection lost, attempting to reconnect...");
                if (reconnect()) {
                    System.out.println("Reconnected to the server");
                    out.println("RECONNECTION");
                    out.println(token);
                } else {
                    System.err.println("Failed to reconnect to the server");
                    running = false;
                }
            }
        }
    }

    private static boolean reconnect() {
        for (int attempt = 1; attempt <= RECONNECT_ATTEMPTS; attempt++) {
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                System.out.println("Reconnection attempt " + attempt + " successful.");
                return true;
            } catch (IOException e) {
                System.err.println("Reconnection attempt " + attempt + " failed.");
                try {
                    TimeUnit.SECONDS.sleep(RECONNECT_DELAY_S);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.err.println("Reconnection delay interrupted.");
                    break;
                }
            }
        }
        return false;
    }

    private static void playGame() {
        try {
            String message;
            while (!(message = in.readLine()).equals("GAME_END")) {
                if (message.equals("PLAY_CARD")) {
                    playCard();
                } else {
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void playCard() {
        try {
            String message;
            while (!(message = in.readLine()).equals("PLAY_CARD_END")) {
                if (message.startsWith("PICK_CARD")) {
                    System.out.println(message.substring(10));

                    BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                    do {
                        String response = userInput.readLine();

                        out.println(response);

                        message = in.readLine();

                        System.out.println(message);
                    } while (message.startsWith("PICK_CARD_ERROR"));
                } else {
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void closeResources() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
