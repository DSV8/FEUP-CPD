package Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GameInstance implements Runnable {
    private ArrayList<ClientHandler> players;
    private Deck deck;
    private HashMap<Card, ClientHandler> cardsInPlay;
    private Card.Suit roundSuit;
    private int currentPlayerIndex;

    public GameInstance(ArrayList<ClientHandler> players) {
        this.players = players;
    }

    private void broadCastMessage(String message) {
        for (ClientHandler player : players) {
            player.sendMessage(message);
        }
    }

    @Override
    public void run() {
        broadCastMessage("GAME_START");
        broadCastMessage("Starting new game\n");

        ClientHandler gameWinner = null;
        while (!isGameOver()) {
            broadCastMessage("Beginning new match\n");
            prepareNewMatch();

            ClientHandler roundWinner = null;
            boolean isMatchOver = false;
            while (!isMatchOver) {
                for (int i = 0; i < players.size(); i++) {
                    playTurn();

                    if (players.get(currentPlayerIndex).getPlayer().getHand().isEmpty()) {
                        isMatchOver = true;
                        break;
                    }

                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                }
                roundWinner = getRoundWinner();
                broadCastMessage(roundWinner.getUser().getUsername() + "wins the round\n");

                endRound();
                currentPlayerIndex = players.indexOf(roundWinner);
            }
            broadCastMessage("Match Over");
            broadCastMessage(roundWinner.getUser().getUsername() + " wins the match\n");

            penalizeLosers();

            broadCastMessage("Players penalty status:");
            for (ClientHandler player : players) {
                broadCastMessage(player.getUser().getUsername() + ": " + player.getPlayer().getPenalty());
            }
            broadCastMessage("");

            gameWinner = roundWinner;
            players = (ArrayList<ClientHandler>) players.stream().filter(player -> !player.getPlayer().isEliminated()).collect(Collectors.toList());
        }
        assert gameWinner != null;
        broadCastMessage(gameWinner.getUser().getUsername() + " won the game. Congratulations!!!\n");
        broadCastMessage("GAME_END");
        gameWinner.getUser().updateLevel(1);
    }

    private void playTurn() {
        ClientHandler currentPlayer = players.get(currentPlayerIndex);

        broadCastMessage(currentPlayer.getUser().getUsername() + "'s turn\n");
        if (roundSuit != null) {
            broadCastMessage("Suit for the round: " + roundSuit + "\n");
        }

        Card card;
        try {
            card = currentPlayer.playCard(roundSuit);
        } catch (IOException e) {
            System.out.println("Error receiving player's card choice" + e.getMessage());
            return;
        }

        if (card == null) {
            broadCastMessage("You don't have a card of the suit: " + roundSuit);
            if (deck.isEmpty()) {
                broadCastMessage("The deck is empty, you cannot draw from it");
                broadCastMessage("Turn passed to the next player\n");
                return;
            } else {
                broadCastMessage("Drawing from the deck:");
                do {
                    card = deck.draw();
                    currentPlayer.getPlayer().receiveCard(card);
                    broadCastMessage(card.getValue() + " of " + card.getSuit());
                } while (!card.getSuit().equals(roundSuit) && !deck.isEmpty());
                broadCastMessage("");
            }
        }

        broadCastMessage(card.getValue() + " of " + card.getSuit() + " played\n");
        cardsInPlay.put(card, currentPlayer);

        if (roundSuit == null) {
            roundSuit = card.getSuit();
        }
    }

    private boolean isGameOver() {
        return players.size() == 1;
    }

    private void prepareNewMatch() {
        deck = new Deck();
        deck.shuffle();

        for (ClientHandler player : players) {
            player.getPlayer().resetHand();
            for (int i = 0; i < 4; i++) {
                player.getPlayer().receiveCard(deck.draw());
            }
        }

        cardsInPlay = new HashMap<>();

        currentPlayerIndex = 0;
    }

    private ClientHandler getRoundWinner() {
        ClientHandler winner = null;
        Card bestCard = null;
        for (Card card : cardsInPlay.keySet()) {
            if (bestCard == null || card.getValue().ordinal() > bestCard.getValue().ordinal()) {
                winner = cardsInPlay.get(card);
                bestCard = card;
            }
        }
        return winner;
    }

    private void endRound() {
        cardsInPlay.clear();
        roundSuit = null;
    }

    private void penalizeLosers() {
        for (ClientHandler player : players) {
            if (!player.getPlayer().getHand().isEmpty()) {
                player.getPlayer().penalize();
            }
        }
    }
}
