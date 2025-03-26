package SolitaireSolver;

import java.util.ArrayList;
import java.util.Stack;

public class Pile {
    Stack<Card> hiddenCards;
    Stack<Card> buildStack;
    Card topCard, bottomCard;

    public Pile() {
        hiddenCards = new Stack<>();
        buildStack = new Stack<>();
    }

    public void dealCard(Card card) {
        hiddenCards.push(card);
    }

    public boolean addToBuildStack(Card card) {
        if (buildStack.isEmpty() || (this.getPile().isEmpty() && card.getRank() == 13)) {
            buildStack.push(card);
            topCard = buildStack.peek();
            return true;
        }
        else {
            if (topCard.getRank() == card.getRank() + 1) {
                if (topCard.isBlack() != card.isBlack()) {
                    buildStack.push(card);
                    topCard = buildStack.peek();
                    return true;
                }
            }
            return false;
        }
    }

    public boolean revealCard() {
        if (!hiddenCards.isEmpty()) {
            buildStack.push(hiddenCards.pop());
            topCard = buildStack.peek();
            return true;
        }
        return false;
    }

    public ArrayList<String> getPile() {
        ArrayList<String> pile = new ArrayList<>();
        for (Card card : hiddenCards) {
            pile.add("-");
        }
        for (Card card : buildStack) {
            pile.add(card.toString());
        }
        return pile;
    }

    public boolean removeTopCard() {
        if (!buildStack.isEmpty()) {
            buildStack.pop();
            this.revealCard();
            return true;
        }
        return false;
    }

    public Card getTopCard() {
        return topCard;
    }

    public Card getBottomCard() {
        return bottomCard;
    }
    public void setBottomCard() {
        if (!buildStack.isEmpty()) {
            bottomCard = buildStack.firstElement();
        }
        else {
            bottomCard = null;
        }
    }

    public Card getBuildCard(int index) {
        return buildStack.get(index);
    }

    public Stack<Card> getBuildStack() {
        return buildStack;
    }
}
