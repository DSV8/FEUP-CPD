package Server;

public class Card {
    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    public enum Value {
        TWO, THREE, FOUR, FIVE, SIX, EIGHT, NINE, TEN, Q, J, K, SEVEN, A
    }

    public enum CardBelongsTo {
        PLAYER, STACK, DECK
    }

    private final Suit suit;
    private final Value value;
    private CardBelongsTo belongsTo;

    public Card(Suit suit, Value value, CardBelongsTo belongsTo) {
        this.suit = suit;
        this.value = value;
        this.belongsTo = belongsTo;
    }

    public Suit getSuit() {
        return suit;
    }

    public Value getValue() {
        return value;
    }

    public CardBelongsTo getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(CardBelongsTo belongsTo) {
        this.belongsTo = belongsTo;
    }
}
