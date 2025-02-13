package SolitaireSolver;

import java.util.Stack;

public class StockWaste {
    Stack<String> stock;
    Stack<String> waste;
    String topCard;

    public StockWaste(Stack<String> cards) {
        stock = cards;
        waste = new Stack<>();
        this.draw();
    }

    public boolean draw() {
        if (!stock.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                waste.push(stock.pop());
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

    public String getTopCard() {
        return topCard;
    }
}
