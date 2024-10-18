package Server;

import java.util.ArrayList;

public class Player {
    private final ArrayList<Card> hand;
    private String penalty;

    public Player() {
        this.hand = new ArrayList<>();
        this.penalty = "";
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public String getPenalty() {
        return penalty;
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public void resetHand() {
        hand.clear();
    }

    public void penalize() {
        if (penalty.length() < 5) {
            penalty += "BURRO".charAt(penalty.length());
        }
    }

    public boolean isEliminated() {
        return penalty.equals("BURRO");
    }

    /*
    public Card playCard(Card.Suit suit) throws IOException {
        sendMessageToPlayer("PLAY_CARD");

        List<Card> validCards = suit == null ? hand : hand.stream().filter(card -> card.getSuit().equals(suit)).toList();

        if (validCards.isEmpty()) {
            return null;
        }

        sendMessageToPlayer("Valid cards in your hand:");
        for (int i = 0; i < validCards.size(); i++) {
            sendMessageToPlayer((i + 1) + ". " + validCards.get(i).getValue() + " of " + validCards.get(i).getSuit());
        }
        sendMessageToPlayer("\n");

        sendMessageToPlayer("PICK_CARD Write the number of the card you want to play:");

        int cardNumber;
        do {
            try {
                cardNumber = Integer.parseInt(receiveMessageFromPlayer());
                if (cardNumber < 1 || cardNumber > validCards.size()) {
                    sendMessageToPlayer("PICK_CARD_ERROR Invalid choice number. Please try again:");
                    continue;
                }
            } catch (NumberFormatException nfe) {
                sendMessageToPlayer("PICK_CARD_ERROR Invalid input. Please enter a number:");
                continue;
            }
            break;
        } while (true);
        sendMessageToPlayer("\n");

        Card chosenCard = validCards.get(cardNumber - 1);

        sendMessageToPlayer("You picked " + chosenCard.getValue() + " of " + chosenCard.getSuit() + "\n");

        hand.remove(chosenCard);

        sendMessageToPlayer("PLAY_CARD_END");

        return chosenCard;
    }*/
}
