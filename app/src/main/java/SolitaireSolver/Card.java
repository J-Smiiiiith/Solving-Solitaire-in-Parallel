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
        if (rank == 1) {
            return "A" + suit;
        }
        else if (rank == 11) {
            return "J" + suit;
        }
        else if (rank == 12) {
            return "Q" + suit;
        }
        else if (rank == 13) {
            return "K" + suit;
        }
        else {
            return rank + "" + suit;
        }
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
