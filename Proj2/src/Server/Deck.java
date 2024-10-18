package Server;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private final ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();

        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Value value : Card.Value.values()) {
                cards.add(new Card(suit, value, Card.CardBelongsTo.DECK));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card draw() {
        Card card = cards.removeLast();
        card.setBelongsTo(Card.CardBelongsTo.PLAYER);
        return card;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
