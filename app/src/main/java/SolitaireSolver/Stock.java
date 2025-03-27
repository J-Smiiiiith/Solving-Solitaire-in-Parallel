package SolitaireSolver;

import java.util.ArrayList;
import SolitaireSolver.Exceptions.EmptyStackException;
import SolitaireSolver.Exceptions.EmptyStockException;

public class Stock {
    ArrayList<Card> stock;
    int cardIndex;

    public Stock(ArrayList<Card> cards) {
        stock = cards;
        cardIndex = 2;
    }

    public Card draw() {
        if (!stock.isEmpty()) {
            Card card;
            if (stock.size() - 1 <= cardIndex) {
                this.setCardIndex(stock.size() - 1);
                card = this.getCard();
                this.setCardIndex(2);
                return card;
            }
            else {
                this.setCardIndex(this.getCardIndex() + 3);
                return this.getCard();
            }
        }
        else {
            throw new EmptyStockException("Stock is empty, cannot draw.");
        }
    }

    public boolean removeTopCard() {
        if (!stock.isEmpty()) {
            stock.remove(cardIndex);
            this.setCardIndex(this.getCardIndex() - 1);
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
}
