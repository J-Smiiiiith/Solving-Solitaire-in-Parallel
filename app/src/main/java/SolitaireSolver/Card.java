package SolitaireSolver;

/**
 * Card class representing a playing card in the game of Solitaire.
 * Each card has a rank, suit, location, and color (black or red).
 */
public class Card {
    int rank, location;
    // Location: 7: Stock, 0-6: Piles
    char suit;
    boolean black;

    /**
     * Constructor for Card.
     * @param rank 1-13
     * @param suit 'S', 'H', 'D', 'C'
     */
    public Card(int rank, char suit) {
        this.rank = rank;
        this.suit = suit;
        black = (suit == 'S' || suit == 'C');
    }

    /**
     * Deep copy constructor for Card.
     * @param other the Card to copy
     */
    public Card(Card other) {
        this.rank = other.rank;
        this.suit = other.suit;
        this.black = other.black;
        this.location = other.location;
    }

    /**
     * Returns a string representation of the card.
     * @return String representation of the card
     */
    @Override
    public String toString() {
        return switch (rank) {
            case 1 -> "A" + suit;
            case 11 -> "J" + suit;
            case 12 -> "Q" + suit;
            case 13 -> "K" + suit;
            default -> rank + "" + suit;
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
