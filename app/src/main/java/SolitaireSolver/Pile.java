package SolitaireSolver;

import SolitaireSolver.Exceptions.EmptyStackException;
import SolitaireSolver.Exceptions.InvalidColourException;
import SolitaireSolver.Exceptions.InvalidRankException;

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
                else {
                    throw new InvalidColourException("Incorrect colour, cannot put two " +
                            (card.isBlack() ? "black" : "red") + " cards on top of each other.");
                }
            }
            else {
                throw new InvalidRankException("Incorrect rank, cannot add card with rank " + card.getRank() +
                        " to build stack with top card rank " + topCard.getRank());
            }
        }
    }

    public boolean revealCard() {
        if (!hiddenCards.isEmpty()) {
            buildStack.push(hiddenCards.pop());
            topCard = buildStack.peek();
            return true;
        }
        throw new EmptyStackException("hiddenCards is empty, cannot reveal card.");
    }

    public boolean removeTopCard() {
        if (!buildStack.isEmpty()) {
            buildStack.pop();
            this.revealCard();
            return true;
        }
        throw new EmptyStackException("buildStack is empty, cannot remove card.");
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
}
