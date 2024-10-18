import java.util.ArrayList;
import java.util.Objects;

public class Print {
    public String mapValue(Card.Value value) {
        return switch (value) {
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            case J -> "J";
            case Q -> "Q";
            case K -> "K";
            case A -> "A";
        };
    }

    public String mapSuit(Card.Suit suit) {
        return switch (suit) {
            case HEARTS -> "♥";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
            case SPADES -> "♠";
        };
    }

    public void printSingleCard(Card card) {
        String s, v;
        s = mapSuit(card.getSuit());
        v = mapValue(card.getValue());

        boolean isCardUp = (card.getBelongsTo() == Card.CardBelongsTo.PLAYER || card.getBelongsTo() == Card.CardBelongsTo.STACK);

        ArrayList<String> cardPrint = new ArrayList<String>();

        if (isCardUp) {
            if (Objects.equals(v, "10")) {
                cardPrint.add("┌─────────┐");
                cardPrint.add("│ " + v + "      │");
                cardPrint.add("│         │");
                cardPrint.add("|    " + s + "    │");
                cardPrint.add("│         │");
                cardPrint.add("│      " + v + "│");
                cardPrint.add("└─────────┘");
            } else {
                cardPrint.add("┌─────────┐");
                cardPrint.add("│ " + v + "       │");
                cardPrint.add("│         │");
                cardPrint.add("|    " + s + "    │");
                cardPrint.add("│         │");
                cardPrint.add("│       " + v + " │");
                cardPrint.add("└─────────┘");
            }
        } else {
            cardPrint.add("┌─────────┐");
            cardPrint.add("│░░░░░░░░░│");
            cardPrint.add("│░░░░░░░░░│");
            cardPrint.add("│░░░░░░░░░│");
            cardPrint.add("│░░░░░░░░░│");
            cardPrint.add("│░░░░░░░░░│");
            cardPrint.add("└─────────┘");
        }
        for (String line : cardPrint) {
            System.out.println(line);
        }
    }

    public void printCards(ArrayList<Card> cards) {
        Card topCard = cards.getFirst();
        String s, v;
        s = mapSuit(topCard.getSuit());
        v = mapValue(topCard.getValue());
        ArrayList<String> cardsPrint = new ArrayList<String>();

        switch (topCard.getBelongsTo()) {
            case PLAYER:
                if (cards.size() == 1) {
                    printSingleCard(topCard);
                } else {
                    if (Objects.equals(v, "10")) {
                        cardsPrint.add("┌─────────┐");
                        cardsPrint.add("│ " + v + "      │");
                        cardsPrint.add("│         │");
                        cardsPrint.add("|    " + s + "    │");
                        cardsPrint.add("│         │");
                        cardsPrint.add("│      " + v + "│");
                        cardsPrint.add("└─────────┘");
                    } else {
                        cardsPrint.add("┌─────────┐");
                        cardsPrint.add("│ " + v + "       │");
                        cardsPrint.add("│         │");
                        cardsPrint.add("|    " + s + "    │");
                        cardsPrint.add("│         │");
                        cardsPrint.add("│       " + v + " │");
                        cardsPrint.add("└─────────┘");
                    }
                    while(!cards.isEmpty()) {
                        Card card = cards.removeFirst();
                        String s1, v1;
                        s1 = mapSuit(card.getSuit());
                        v1 = mapValue(card.getValue());
                        if (Objects.equals(v1, "10")) {
                            cardsPrint.set(0, cardsPrint.get(0) + "─────┐");
                            cardsPrint.set(1, cardsPrint.get(1) + "     │");
                            cardsPrint.set(2, cardsPrint.get(2) + "     │");
                            cardsPrint.set(3, cardsPrint.get(3) + s1 + "    │");
                            cardsPrint.set(4, cardsPrint.get(4) + "     │");
                            cardsPrint.set(5, cardsPrint.get(5) + "  " + v1 + " │");
                            cardsPrint.set(6, cardsPrint.get(6) + "─────┘");
                        } else {
                            cardsPrint.set(0, cardsPrint.get(0) + "─────┐");
                            cardsPrint.set(1, cardsPrint.get(1) + "     │");
                            cardsPrint.set(2, cardsPrint.get(2) + "     │");
                            cardsPrint.set(3, cardsPrint.get(3) + s1 + "    │");
                            cardsPrint.set(4, cardsPrint.get(4) + "     │");
                            cardsPrint.set(5, cardsPrint.get(5) + "   " + v1 + " │");
                            cardsPrint.set(6, cardsPrint.get(6) + "─────┘");
                        }
                    }
                    for (String line : cardsPrint) {
                        System.out.println(line);
                    }
                }
                break;
            case STACK:
                if (cards.size() == 1) {
                    printSingleCard(topCard);
                } else if (cards.size() == 2) {
                    if (Objects.equals(v, "10")) {
                        cardsPrint.add("┌─────────┐┐");
                        cardsPrint.add("│ " + v + "      ││");
                        cardsPrint.add("│         ││");
                        cardsPrint.add("|    " + s + "    ││");
                        cardsPrint.add("│         ││");
                        cardsPrint.add("│      " + v + "││");
                        cardsPrint.add("└─────────┘┘");
                    } else {
                        cardsPrint.add("┌─────────┐┐");
                        cardsPrint.add("│ " + v + "       ││");
                        cardsPrint.add("│         ││");
                        cardsPrint.add("|    " + s + "    ││");
                        cardsPrint.add("│         ││");
                        cardsPrint.add("│       " + v + " ││");
                        cardsPrint.add("└─────────┘┘");
                    }
                    for (String line : cardsPrint) {
                        System.out.println(line);
                    }
                } else if (cards.size() == 3) {
                    if (Objects.equals(v, "10")) {
                        cardsPrint.add("┌─────────┐┐┐");
                        cardsPrint.add("│ " + v + "      │││");
                        cardsPrint.add("│         │││");
                        cardsPrint.add("|    " + s + "    │││");
                        cardsPrint.add("│         │││");
                        cardsPrint.add("│      " + v + "│││");
                        cardsPrint.add("└─────────┘┘┘");
                    } else {
                        cardsPrint.add("┌─────────┐┐┐");
                        cardsPrint.add("│ " + v + "       │││");
                        cardsPrint.add("│         │││");
                        cardsPrint.add("|    " + s + "    │││");
                        cardsPrint.add("│         │││");
                        cardsPrint.add("│       " + v + " │││");
                        cardsPrint.add("└─────────┘┘┘");
                    }
                    for (String line : cardsPrint) {
                        System.out.println(line);
                    }
                } else if (cards.size() == 4) {
                    if (Objects.equals(v, "10")) {
                        cardsPrint.add("┌─────────┐┐┐┐");
                        cardsPrint.add("│ " + v + "      ││││");
                        cardsPrint.add("│         ││││");
                        cardsPrint.add("|    " + s + "    ││││");
                        cardsPrint.add("│         ││││");
                        cardsPrint.add("│      " + v + "││││");
                        cardsPrint.add("└─────────┘┘┘┘");
                    } else {
                        cardsPrint.add("┌─────────┐┐┐┐");
                        cardsPrint.add("│ " + v + "       ││││");
                        cardsPrint.add("│         ││││");
                        cardsPrint.add("|    " + s + "    ││││");
                        cardsPrint.add("│         ││││");
                        cardsPrint.add("│       " + v + " ││││");
                        cardsPrint.add("└─────────┘┘┘┘");
                    }
                    for (String line : cardsPrint) {
                        System.out.println(line);
                    }
                } else {
                    if (Objects.equals(v, "10")) {
                        cardsPrint.add("┌─────────┐┐┐┐┐");
                        cardsPrint.add("│ " + v + "      │││││");
                        cardsPrint.add("│         │││││");
                        cardsPrint.add("|    " + s + "    │││││");
                        cardsPrint.add("│         │││││");
                        cardsPrint.add("│      " + v + "│││││");
                        cardsPrint.add("└─────────┘┘┘┘┘");
                    } else {
                        cardsPrint.add("┌─────────┐┐┐┐┐");
                        cardsPrint.add("│ " + v + "       │││││");
                        cardsPrint.add("│         │││││");
                        cardsPrint.add("|    " + s + "    │││││");
                        cardsPrint.add("│         │││││");
                        cardsPrint.add("│       " + v + " │││││");
                        cardsPrint.add("└─────────┘┘┘┘┘");
                    }
                    for (String line : cardsPrint) {
                        System.out.println(line);
                    }
                }
                break;
            case DECK:
                if (cards.size() == 1) {
                    printSingleCard(topCard);
                } else if (cards.size() == 2) {
                    cardsPrint.add("┌─────────┐┐");
                    cardsPrint.add("│░░░░░░░░░││");
                    cardsPrint.add("│░░░░░░░░░││");
                    cardsPrint.add("│░░░░░░░░░││");
                    cardsPrint.add("│░░░░░░░░░││");
                    cardsPrint.add("│░░░░░░░░░││");
                    cardsPrint.add("└─────────┘┘");

                    for (String line : cardsPrint) {
                        System.out.println(line);
                    }
                } else if (cards.size() == 3) {
                    cardsPrint.add("┌─────────┐┐┐");
                    cardsPrint.add("│░░░░░░░░░│││");
                    cardsPrint.add("│░░░░░░░░░│││");
                    cardsPrint.add("│░░░░░░░░░│││");
                    cardsPrint.add("│░░░░░░░░░│││");
                    cardsPrint.add("│░░░░░░░░░│││");
                    cardsPrint.add("└─────────┘┘┘");

                    for (String line : cardsPrint) {
                        System.out.println(line);
                    }
                } else if (cards.size() == 4) {
                    cardsPrint.add("┌─────────┐┐┐┐");
                    cardsPrint.add("│░░░░░░░░░││││");
                    cardsPrint.add("│░░░░░░░░░││││");
                    cardsPrint.add("│░░░░░░░░░││││");
                    cardsPrint.add("│░░░░░░░░░││││");
                    cardsPrint.add("│░░░░░░░░░││││");
                    cardsPrint.add("└─────────┘┘┘┘");

                    for (String line : cardsPrint) {
                        System.out.println(line);
                    }
                } else {
                    cardsPrint.add("┌─────────┐┐┐┐┐");
                    cardsPrint.add("│░░░░░░░░░│││││");
                    cardsPrint.add("│░░░░░░░░░│││││");
                    cardsPrint.add("│░░░░░░░░░│││││");
                    cardsPrint.add("│░░░░░░░░░│││││");
                    cardsPrint.add("│░░░░░░░░░│││││");
                    cardsPrint.add("└─────────┘┘┘┘┘");

                    for (String line : cardsPrint) {
                        System.out.println(line);
                    }
                }
                break;
        }
    }
}
