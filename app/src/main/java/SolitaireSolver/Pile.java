package SolitaireSolver;

import SolitaireSolver.Exceptions.EmptyStackException;
import SolitaireSolver.Exceptions.InvalidColourException;
import SolitaireSolver.Exceptions.InvalidRankException;

import java.util.ArrayList;
import java.util.Stack;

public class Pile {
    Stack<Card> hiddenCards;
    Stack<Card> buildStack;

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
            return true;
        }
        else {
            if (this.getTopCard().getRank() == card.getRank() + 1) {
                if (this.getTopCard().isBlack() != card.isBlack()) {
                    buildStack.push(card);
                    return true;
                }
                else {
                    throw new InvalidColourException("Incorrect colour, cannot put two " +
                            (card.isBlack() ? "black" : "red") + " cards on top of each other.");
                }
            }
            else {
                throw new InvalidRankException("Incorrect rank, cannot add card with rank " + card.getRank() +
                        " to build stack with top card rank " + this.getTopCard().getRank());
            }
        }
    }

    public boolean revealCard() {
        if (!hiddenCards.isEmpty() && buildStack.isEmpty()) {
            buildStack.push(hiddenCards.pop());
            return true;
        }
        throw new EmptyStackException("hiddenCards is empty, cannot reveal card.");
    }

    public boolean removeTopCard() {
        if (!buildStack.isEmpty()) {
            buildStack.pop();
            try {
                this.revealCard();
            } catch (EmptyStackException e) {
                // Do nothing
            }
            return true;
        }
        throw new EmptyStackException("buildStack is empty, cannot remove card.");
    }

    public Card getTopCard() {
        if (!buildStack.isEmpty()) {
            return buildStack.peek();
        }
        else {
            return null;
        }
    }

    public Card getTopHiddenCard() {
        if (!hiddenCards.isEmpty()) {
            return hiddenCards.peek();
        }
        else {
            return null;
        }
    }

    public Card getBottomCard() {
        if (buildStack.isEmpty()) {
            return null;
        }
        return buildStack.getFirst();
    }

    public Card getBuildCard(int index) {
        return buildStack.get(index);
    }

    public Stack<Card> getBuildStack() {
        return buildStack;
    }
    public void setBuildStack(ArrayList<Card> cards) {
        buildStack = new Stack<>();
        buildStack.addAll(cards);
    }

    public Stack<Card> getHiddenCards() {
        return hiddenCards;
    }
    public void setHiddenCards(ArrayList<Card> cards) {
        hiddenCards = new Stack<>();
        hiddenCards.addAll(cards);
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

    public String getOutputPile() {
        return hiddenCards + "\t\t" + buildStack;
    }

    public String getPileState() {
        String piles = "";
        for (Card card : hiddenCards) {
            piles += card.toString();
        }
        for (Card card : buildStack) {
            piles += card.toString();
        }
        return piles;
    }

    public int getCardIndex(Card card) {
        return buildStack.indexOf(card);
    }

    public Card getCardAtIndex(int index) {
        if (index < buildStack.size()) {
            return buildStack.get(index);
        }
        return null;
    }

    public void updateCardLocations(int location) {
        for (Card card : hiddenCards) {
            card.setLocation(location);
        }
        for (Card card : buildStack) {
            card.setLocation(location);
        }
    }
}
