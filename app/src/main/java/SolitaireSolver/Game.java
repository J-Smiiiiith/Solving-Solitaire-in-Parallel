package SolitaireSolver;

import java.util.Collections;
import java.util.Stack;

public class Game {
    char[] suits;
    Stack<Card> deck;
    Pile[] piles;
    Foundation foundation;
    StockWaste stockWaste;

    private Game() {
        suits = new char[] {'C', 'S', 'H', 'D'};
        deck = buildDeck();
        this.shuffleDeck();
        piles = new Pile[] {new Pile(), new Pile(), new Pile(), new Pile(), new Pile(), new Pile(), new Pile()};
        foundation = new Foundation();

        this.buildDeck();
        this.dealCards();
        stockWaste = new StockWaste(deck);
    }

    private Stack<Card> buildDeck() {
        deck = new Stack<>();
        for (char suit : suits) {
            for (int i = 1; i <= 13; i++) {
                deck.push(new Card(i, suit));
            }
        }
        return deck;
    }

    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    private void dealCards() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j <= i; j++) {
                piles[i].dealCard(deck.pop());
            }
            piles[i].revealCard();
        }
    }

    @Override
    public String toString() {
        String output = "";

        output += stockWaste.getTopCard() + "\t\t\t" + foundation.getClubCard() + " " + foundation.getSpadeCard() + " " +
                foundation.getHeartCard() + " " + foundation.getDiamondCard() + "\n\n";

        for (Pile pile : piles) {
            output += pile.getPile() + "\n";
        }
        return output;
    }

    public static void main(String[] args) {
        Game game = new Game();
        System.out.println(game);
    }
}