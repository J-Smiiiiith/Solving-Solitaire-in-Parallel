package SolitaireSolver;

import java.util.*;

import SolitaireSolver.Exceptions.InvalidMoveException;
import SolitaireSolver.Exceptions.InvalidSuitException;

public class Solitaire {
    char[] suits;
    Stack<Card> deck;
    Pile[] piles;
    Foundation foundation;
    Stock stock;

    public Solitaire() {
        suits = new char[]{'C', 'S', 'H', 'D'};
        deck = buildDeck();
        this.shuffleDeck();
        piles = new Pile[]{new Pile(), new Pile(), new Pile(), new Pile(), new Pile(), new Pile(), new Pile()};
        foundation = new Foundation();

        this.dealCards();

        ArrayList<Card> newDeck = new ArrayList<>(deck);
        Collections.reverse(newDeck);

        stock = new Stock(newDeck);
    }

    private Stack<Card> buildDeck() {
        deck = new Stack<>();
        for (char suit : suits) {
            for (int i = 1; i <= 13; i++) {
                deck.push(new Card(i, suit));
            }
        }
        return deck;
    }

    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    private void dealCards() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j <= i; j++) {
                piles[i].dealCard(deck.pop());
                piles[i].getTopHiddenCard().setLocation(i);
            }
            piles[i].revealCard();
            piles[i].setBottomCard();
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
                src.setBottomCard();
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
        Card card = stock.draw();

        while (!usableCards.contains(card)) {
            usableCards.add(card);
            card = stock.draw();
        }

        for (Pile pile : piles) {
            if (!pile.getBuildStack().isEmpty()) {
                usableCards.addAll(pile.getBuildStack());
            }
        }
        return usableCards;
    }

    private ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        ArrayList<Card> usableCards = getUsableCards();
        System.out.println("Usable Cards: \t\t" + usableCards);
        int rank;

        for (Card card : usableCards) {
            rank = switch (card.getSuit()) {
                case 'C' -> foundation.getClubs();
                case 'S' -> foundation.getSpades();
                case 'D' -> foundation.getDiamonds();
                case 'H' -> foundation.getHearts();
                default -> throw new InvalidSuitException("Invalid suit: Valid suits include ['H', 'D', 'C', 'S'], not "
                        + card.getSuit());
            };
            if (card.getRank() == rank + 1) {
                possibleMoves.add(new Move(card));
            }
            //Check eligibility for a foundation move

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
            //Check eligibility for a pile move
        }
        return possibleMoves;
    }

    public void makeMove(Move move) {
        if (move.getDst() == null) {
            foundation.toFoundation(move.getCard());
            if (move.getCard().getLocation() != 7) {
                piles[move.getCard().getLocation()].removeTopCard();
            } //pile to foundation
            else {
                stock.removeCard(move.getCard().getLocation());
            } //stock to foundation
        } //Moves to foundation
        else {
            if (move.getCard().getLocation() == 7) {
                move.getDst().addToBuildStack(move.getCard());
                move.getCard().setLocation(this.getPileNum(move.getDst()));
                stock.removeCard(stock.getCardIndex(move.getCard()));
            } // Stock to pile
            else if (piles[move.getCard().getLocation()].getBottomCard() == move.getCard()) {
                this.moveEntireBuildStack(piles[move.getCard().getLocation()], move.getDst());
            } // Pile to pile: Move entire pile
            else {
                this.movePartialBuildStack(piles[move.getCard().getLocation()], move.getDst(),
                        piles[move.getCard().getLocation()].getCardIndex(move.getCard()));
            } // Pile to pile: Move partial pile
        } //Moves to pile
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

    @Override
    public String toString() {
        String output = "";

        output += stock.getCard() + "\t\t\t" + foundation.getClubCard() + " " + foundation.getSpadeCard() +
                " " + foundation.getHeartCard() + " " + foundation.getDiamondCard() + "\n" + "\n\n";

        for (Pile pile : piles) {
            output += pile.getPile() + "\n";
        }
        return output;
    }

    public boolean randomSolitaireSolver() {
        boolean end = false;
        Queue<String> gameStates = new LinkedList<>();
        gameStates.add(this.getGameState());

        while (!end) {
            System.out.println(this + "\n");
            System.out.println("Stock: \t\t\t\t" + stock.getStock());
            ArrayList<Move> possibleMoves = this.getPossibleMoves();
            System.out.println("Possible Moves: \t" + possibleMoves + "\n");
            for (String state : gameStates) {
                System.out.println(state);
            }

            if (possibleMoves.isEmpty()) {
                System.out.println("Game lost: No possible moves");
                return false;
            }

            int randomInt = (int) (Math.random() * possibleMoves.size());
            makeMove(possibleMoves.get(randomInt));

            String currentState = this.getGameState();

            if (gameStates.contains(currentState)) {
                System.out.println("Game lost: Repeated game state detected.");
                return false;
            } else if (foundation.checkWin()) {
                System.out.println("Game lost: Foundation win.");
                return true;
            } else {
                if (gameStates.size() > 4) {
                    gameStates.poll();
                }
                gameStates.add(currentState);
            }
        }
        return end;
    }
}