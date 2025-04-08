package SolitaireSolver;

public class Card {
    int rank, location;
    // Location: 7 : Stock, 0-6: Piles
    char suit;
    boolean black;


    public Card(int rank, char suit) {
        this.rank = rank;
        this.suit = suit;
        black = (suit == 'S' || suit == 'C');
    }

    @Override
    public String toString() {
        return switch (rank) {
            case 1 -> "A" + suit + location;
            case 11 -> "J" + suit + location;
            case 12 -> "Q" + suit + location;
            case 13 -> "K" + suit + location;
            default -> rank + "" + suit + location;
        };
    }

    public int getRank() {
        return rank;
    }

    public char getSuit() {
        return suit;
    }

    public boolean isBlack() {
        return black;
    }

    public int getLocation() {
        return location;
    }
    public void setLocation(int location) {
        this.location = location;
    }
}
