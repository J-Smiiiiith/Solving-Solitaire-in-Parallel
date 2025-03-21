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
            if (waste.size() < 3) {
                for (int i = 0; i < stock.size(); i++) {
                    waste.push(stock.pop());
                }
            }
            else {
                for (int i = 0; i < 4; i++) {
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
            for (int i = 0; i < waste.size(); i++) {
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
}
