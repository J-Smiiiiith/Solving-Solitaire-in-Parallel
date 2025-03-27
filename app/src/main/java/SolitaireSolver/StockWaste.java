package SolitaireSolver;

import java.util.ArrayList;
import java.util.Stack;
import SolitaireSolver.Exceptions.EmptyStackException;
import SolitaireSolver.Exceptions.EmptyStockException;
import SolitaireSolver.Exceptions.NonEmptyStackException;

public class StockWaste {
    Stack<Card> stock;
    Stack<Card> waste;
    Card topCard;

    ArrayList<Card> newStock;
    int cardIndex;

    public StockWaste(Stack<Card> cards) {
        //System.out.println("Old: " + cards);
        stock = cards;
        waste = new Stack<>();
        this.draw();
    }

    public StockWaste(ArrayList<Card> cards) {
        //System.out.println("New: " + cards);
        newStock = cards;
        cardIndex = 2;
    }

    public Card newDraw() {
        if (!newStock.isEmpty()) {
            Card card;
            if (newStock.size() - 1 <= cardIndex) {
                this.setCardIndex(newStock.size() - 1);
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

    public boolean draw() {
        if (!stock.isEmpty()) {
            if (this.getStockSize() < 3) {
                int size = this.getStockSize();
                for (int i = 0; i < size; i++) {
                    waste.push(stock.pop());
                }
            }
            else {
                for (int i = 0; i < 3; i++) {
                    waste.push(stock.pop());
                }
            }
            topCard = waste.peek();
            return true;
        }
        throw new EmptyStackException("Stock is empty, cannot draw.");
    }

    public boolean replenish() {
        if (stock.isEmpty()) {
            while (!waste.isEmpty()) {
                stock.push(waste.pop());
            }
            this.draw();
            return true;
        }
        throw new NonEmptyStackException("Stock is not empty, cannot replenish.");
    }

    public boolean removeTopCard() {
        if (!waste.isEmpty()) {
            waste.pop();
            topCard = waste.isEmpty() ? null : waste.peek();
            return true;
        }
        throw new EmptyStackException("Waste is empty, cannot remove top card.");
    }

    public Card getTopCard() {
        return topCard;
    }

    public int getStockSize() {
        return stock.size();
    }

    public int getWasteSize() {
        return waste.size();
    }

    public int getCardIndex() {
        return cardIndex;
    }
    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public Card getCard() {
        return newStock.get(cardIndex);
    }

    public int getSize() {
        return newStock.size();
    }
}
