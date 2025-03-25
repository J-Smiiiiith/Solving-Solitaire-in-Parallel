package SolitaireSolver;

import java.util.Stack;

public class StockWaste {
    Stack<Card> stock;
    Stack<Card> waste;
    Card topCard;

    public StockWaste(Stack<Card> cards) {
        stock = cards;
        waste = new Stack<>();
        this.draw();
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
        return false;
    }

    public boolean replenish() {
        if (stock.isEmpty()) {
            while (!waste.isEmpty()) {
                stock.push(waste.pop());
            }
            this.draw();
            return true;
        }
        return false;
    }

    public boolean removeTopCard() {
        if (!waste.isEmpty()) {
            waste.pop();
            topCard = waste.peek();
            return true;
        }
        return false;
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
}
