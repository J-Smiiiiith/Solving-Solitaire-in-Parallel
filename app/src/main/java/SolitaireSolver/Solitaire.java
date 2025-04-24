package SolitaireSolver;

import java.util.*;

import SolitaireSolver.Exceptions.EmptyStockException;
import SolitaireSolver.Exceptions.InvalidMoveException;
import SolitaireSolver.Exceptions.InvalidSuitException;

public class Solitaire {
    Stack<Card> deck;
    Pile[] piles;
    Foundation foundation;
    Stock stock;

    public Solitaire(Deck deck) {
        this.deck = deck.getDeck();
        piles = new Pile[]{new Pile(), new Pile(), new Pile(), new Pile(), new Pile(), new Pile(), new Pile()};
        foundation = new Foundation();

        this.dealCards();

        ArrayList<Card> newDeck = new ArrayList<>(this.deck);
        Collections.reverse(newDeck);

        stock = new Stock(newDeck);
    }

    public Solitaire(Solitaire other) {
        this.foundation = new Foundation(other.foundation);

        this.piles = new Pile[other.piles.length];
        for (int i = 0; i < other.piles.length; i++) {
            this.piles[i] = new Pile(other.piles[i]);
        }

        this.stock = new Stock(other.stock);
    } // Deep copy for solitaire

    private void dealCards() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j <= i; j++) {
                piles[i].dealCard(deck.pop());
                piles[i].getTopHiddenCard().setLocation(i);
            }
            piles[i].revealCard();
        }
    }

    private void moveEntireBuildStack(Pile src, Pile dst) {
        int rankCheck;
        if (dst.getPile().isEmpty()) {
            rankCheck = 0;
        } else {
            rankCheck = dst.getTopCard().getRank() - 1;
        }
        if ((src.getBottomCard().getRank() == rankCheck) ||
                ((src.getBottomCard().getRank() == 13) && (rankCheck == 0))) {
            Stack<Card> tmpStack = new Stack<>();
            while (!src.getBuildStack().isEmpty()) {
                tmpStack.push(src.getBuildStack().pop());
            }
            addStackToBuildStack(tmpStack, dst);
            if (!src.getHiddenCards().isEmpty()) {
                src.revealCard();
            }
        } else {
            throw new InvalidMoveException("Invalid move: Cannot move build stack");
        }
    }

    private void movePartialBuildStack(Pile src, Pile dst, int cardNum) {
        int rankCheck;
        if (dst.getPile().isEmpty()) {
            rankCheck = 0;
        } else {
            rankCheck = dst.getTopCard().getRank() - 1;
        }
        if ((src.getBuildCard(cardNum).getRank() == rankCheck) ||
                ((src.getBuildCard(cardNum).getRank() == 13) && (rankCheck == 0))) {
            Stack<Card> tmpStack = new Stack<>();
            int size = src.getBuildStack().size();
            for (int i = cardNum; i < size; i++) {
                tmpStack.push(src.getBuildStack().pop());
            }
            addStackToBuildStack(tmpStack, dst);
        } else {
            throw new InvalidMoveException("Invalid move: Cannot move partial build stack");
        }
    }

    private void addStackToBuildStack(Stack<Card> stack, Pile dst) {
        while (!stack.isEmpty()) {
            stack.peek().setLocation(this.getPileNum(dst));
            dst.addToBuildStack(stack.pop());
        }
    }

    public int getPileNum(Pile p) {
        for (int i = 0; i < piles.length; i++) {
            if (piles[i] == p) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<Card> getUsableCards() {
        ArrayList<Card> usableCards = new ArrayList<>();

        try {
            Card card = stock.draw();
            while (!usableCards.contains(card)) {
                usableCards.add(card);
                card = stock.draw();
            }
        } catch (EmptyStockException e) {
            // Do nothing, stock is empty
        }

        for (Pile pile : piles) {
            if (!pile.getBuildStack().isEmpty()) {
                usableCards.addAll(pile.getBuildStack());
            }
        }
        return usableCards;
    }

    public ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        ArrayList<Card> usableCards = getUsableCards();
        //System.out.println("Usable Cards: " + usableCards);

        for (Card card : usableCards) {
            if (card.getLocation() != 7) {
                if (card.equals(piles[card.getLocation()].getTopCard())) {
                    checkRankForFoundationMove(possibleMoves, card);
                }
            } //Check that card is at top of the pile for foundation move.
            else {
                checkRankForFoundationMove(possibleMoves, card);
            }

            for (Pile pile : piles) {
                Card topCard = pile.getTopCard();
                if (!pile.getBuildStack().isEmpty()) {
                    if (topCard.isBlack() != card.isBlack()) {
                        if (card.getRank() == topCard.getRank() - 1) {
                            possibleMoves.add(new Move(card, pile));
                        }
                    }
                } else {
                    if (card.getRank() == 13) {
                        possibleMoves.add(new Move(card, pile));
                    }
                }
            }
        }

        for (Move move : possibleMoves) {
            move.determineMoveType(piles);
        }

        return possibleMoves;
    }

    private void checkRankForFoundationMove(ArrayList<Move> possibleMoves, Card card) {
        int rank;
        rank = switch (card.getSuit()) {
            case 'C' -> foundation.getClubs();
            case 'S' -> foundation.getSpades();
            case 'D' -> foundation.getDiamonds();
            case 'H' -> foundation.getHearts();
            default -> throw new InvalidSuitException("Invalid suit: Valid suits include " +
                    "['H', 'D', 'C', 'S'], not " + card.getSuit());
        };
        if (card.getRank() == rank + 1) {
            possibleMoves.add(new Move(card));
        }
    }

    public void makeMove(Move move) {
        switch (move.getMoveType()) {
            case 0:
                foundation.toFoundation(move.getCard());
                if (move.getCard().getLocation() != 7) {
                    piles[move.getCard().getLocation()].removeTopCard();
                } //pile to foundation
                else {
                    stock.removeCard(stock.getCardStockIndex(move.getCard()));
                } //stock to foundation
                break;
            case 1:
                move.getDst().addToBuildStack(move.getCard());
                move.getCard().setLocation(this.getPileNum(move.getDst()));
                stock.removeCard(stock.getCardStockIndex(move.getCard()));
                break;
            //stock to pile
            case 2:
                this.moveEntireBuildStack(piles[move.getCard().getLocation()], move.getDst());
                break;
            //pile to pile: Move entire pile
            case 3:
                this.movePartialBuildStack(piles[move.getCard().getLocation()], move.getDst(),
                        piles[move.getCard().getLocation()].getCardIndex(move.getCard()));
                break;
            //pile to pile: Move partial pile
        }
    }

    public Move getBestMove(ArrayList<Move> moves) {
        Move bestMove = moves.getFirst();
        for (Move move : moves) {
            if (move.getHeuristic() > bestMove.getHeuristic()) {
                bestMove = move;
            }
        }
        return bestMove;
    }

    public Move getBestMoveWithPriority(ArrayList<Move> moves) {
        ArrayList<Move> bestMoves = new ArrayList<>();
        bestMoves.add(moves.getFirst());
        for (Move move : moves) {
            if (move.getHeuristic() > bestMoves.getFirst().getHeuristic()) {
                bestMoves.clear();
                bestMoves.add(move);
            } else if (bestMoves.getFirst().getHeuristic() == move.getHeuristic()) {
                if (!bestMoves.getFirst().equals(move)) {
                    bestMoves.add(move);
                }
            }
        }
        Move bestMovePriority = bestMoves.getFirst();
        for (Move move : bestMoves) {
            if (move.getPriority() > bestMovePriority.getPriority()) {
                bestMovePriority = move;
            }
        }
        return bestMovePriority;
    }

    public Move getBestMoveMonetCarlo(ArrayList<Move> moves) {
        Move bestMove = moves.getFirst();
        for (Move move : moves) {
            if (move.getMonteCarloScore() > bestMove.getMonteCarloScore()) {
                bestMove = move;
            }
            if (move.getMonteCarloScore() == bestMove.getMonteCarloScore()) {
                if (((int) (Math.random() * 2)) == 0) {
                    bestMove = move;
                } // Randomly select between two moves with the same score
            }
        }
        return bestMove;
    }

    public void resetMonteCarloScores(ArrayList<Move> moves) {
        for (Move move : moves) {
            move.resetMonteCarloScore();
        }
    }

    public boolean checkWin() {
        if (stock.getStock().isEmpty()) {
            for (Pile p : piles) {
                if (!p.getHiddenCards().isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public String getGameState() {
        String gameState = "";
        for (Card card : stock.getStock()) {
            gameState += card;
        }
        for (Pile pile : piles) {
            gameState += pile.getPileState();
        }
        gameState += foundation.getFoundationState();
        return gameState;
    }

    public Foundation getFoundation() {
        return foundation;
    }
    public Pile[] getPiles() {
        return piles;
    }
    public Stock getStock() {
        return stock;
    }

    public int getRemainingCards() {
        int foundationCards = foundation.getClubs() + foundation.getDiamonds() + foundation.getHearts() +
                foundation.getSpades();
        return 52 - foundationCards;
    }

    @Override
    public String toString() {
        String output = "";

        output += "Stock: " + stock.stock + " \n" + foundation.getClubCard() + " " + foundation.getSpadeCard() +
                " " + foundation.getHeartCard() + " " + foundation.getDiamondCard() + "\n\n";

        for (Pile pile : piles) {
            output += pile.getOutputPile() + "\n";
        }
        return output;
    }
}