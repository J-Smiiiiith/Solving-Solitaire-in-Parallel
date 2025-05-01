package SolitaireSolver;

import java.util.ArrayList;
import SolitaireSolver.Exceptions.EmptyStackException;
import SolitaireSolver.Exceptions.EmptyStockException;

/**
 * Stock class representing the stock in the game of Solitaire.
 * The stock contains cards that can be drawn during the game.
 */
public class Stock {
    ArrayList<Card> stock;
    int cardIndex;

    final int STARTING_INDEX = 2;

    /**
     * Constructor for Stock.
     * Initializes the stock with a given list of cards.
     * @param cards the list of cards to initialize the stock
     */
    public Stock(ArrayList<Card> cards) {
        stock = cards;
        cardIndex = STARTING_INDEX;

        for (Card card : stock) {
            card.setLocation(7);
        }
    }

    /**
     * Deep copy constructor for Stock.
     * @param other the Stock to copy
     */
    public Stock(Stock other) {
        this.stock = new ArrayList<>();
        for (Card card : other.getStock()) {
            this.stock.add(new Card(card));
        }
        this.cardIndex = other.getCardIndex();
        this.updateCardLocations();
    }

    /**
     * Draws a card from the stock.
     * If the stock is empty, throws an EmptyStockException.
     * @return the drawn card
     */
    public Card draw() {
        if (!stock.isEmpty()) {
            Card card;
            card = this.getCard();
            if ((this.getCardIndex() == stock.size() - 1) && (stock.size() > 2)) {
                this.setCardIndex(STARTING_INDEX);
            } else {
                this.setCardIndex(Math.min(this.getCardIndex() + 3, stock.size() - 1));
            }
            return card;
        } else {
            throw new EmptyStockException("Stock is empty, cannot draw card.");
        }
    }

    public boolean removeCard(int index) {
        if (!stock.isEmpty()) {
            stock.remove(index);
            if (index > 0) {
                this.setCardIndex(index - 1);
            } else {
                this.setCardIndex(0);
            }
            return true;
        }
        throw new EmptyStockException("Stock is empty, cannot remove top card.");
    }

    public int getCardIndex() {
        return cardIndex;
    }
    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public Card getCard() {
        return stock.get(cardIndex);
    }

    public int getCardStockIndex(Card card) {
        return stock.indexOf(card);
    }

    public ArrayList<Card> getStock() {
        return stock;
    }
    public void setStock(ArrayList<Card> cards) {
        stock = new ArrayList<>(cards);
    }

    public void updateCardLocations() {
        for (Card card : stock) {
            card.setLocation(7);
        }
    }
}
