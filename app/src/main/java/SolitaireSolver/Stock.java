package SolitaireSolver;

import java.util.ArrayList;
import SolitaireSolver.Exceptions.EmptyStackException;
import SolitaireSolver.Exceptions.EmptyStockException;

public class Stock {
    ArrayList<Card> stock;
    int cardIndex;

    final int STARTING_INDEX = 2;

    public Stock(ArrayList<Card> cards) {
        stock = cards;
        cardIndex = STARTING_INDEX;

        for (Card card : stock) {
            card.setLocation(7);
        }
    }

    public Card draw() {
        if (!stock.isEmpty()) {
            this.setCardIndex(this.getCardIndex() + 3);
            Card card;
            if (stock.size() - 1 <= cardIndex) {
                this.setCardIndex(stock.size() - 1);
                card = this.getCard();
                this.setCardIndex(Math.min(stock.size() - 1, STARTING_INDEX));
                return card;
            } else {
                card = this.getCard();
                return card;
            }
        } else {
            throw new EmptyStockException("Stock is empty, cannot draw.");
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

    public int getCardIndex(Card card) {
        return stock.indexOf(card);
    }
}
