package SolitaireSolver;

import java.util.Collections;
import java.util.Stack;

public class Deck {
    public Stack<Card> deck;

    public Deck() {
        deck = new Stack<>();
        char[] SUITS = {'C', 'D', 'H', 'S'};
        int MAX_RANK = 13;

        for (char suit : SUITS) {
            for (int rank = 1; rank <= MAX_RANK; rank++) {
                deck.push(new Card(rank, suit));
            }
        }
        this.shuffleDeck();
    }

    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public Stack<Card> getDeck() {
        return deck;
    }
}
