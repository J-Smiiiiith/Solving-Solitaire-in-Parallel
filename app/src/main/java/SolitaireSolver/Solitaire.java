package SolitaireSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;
import SolitaireSolver.Exceptions.InvalidMoveException;
import SolitaireSolver.Exceptions.InvalidSuitException;

public class Solitaire {
    char[] suits;
    Stack<Card> deck;
    Pile[] piles;
    Foundation foundation;
    Stock stock;

    public Solitaire() {
        suits = new char[] {'C', 'S', 'H', 'D'};
        deck = buildDeck();
        this.shuffleDeck();
        piles = new Pile[] {new Pile(), new Pile(), new Pile(), new Pile(), new Pile(), new Pile(), new Pile()};
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
            }
            piles[i].revealCard();
            piles[i].setBottomCard();
        }
    }

    private void stockToPile(Pile pile) {
        if (pile.addToBuildStack(this.stock.getCard())) {
            this.stock.removeTopCard();
        }
        else {
            throw new InvalidMoveException("Invalid move: Cannot move card from stock to pile");
        }
    }

    private void stockToFoundation() {
        if (this.foundation.toFoundation(this.stock.getCard())) {
            this.stock.removeTopCard();
        }
        else {
            throw new InvalidMoveException("");
        }
    }

    private void pileToFoundation(Pile pile) {
        if (this.foundation.toFoundation(pile.getTopCard())) {
            pile.removeTopCard();
            pile.setBottomCard();
        }
        else {
            throw new InvalidMoveException("Invalid move: Cannot move card from pile to foundation");
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
            src.revealCard();
            src.setBottomCard();
        }
        else {
            throw new InvalidMoveException("Invalid move: Cannot move build stack");
        }
    }

    private void movePartialBuildStack(Pile src, Pile dst, int cardNum) {
        if (src.getBuildCard(cardNum).getRank() == dst.getTopCard().getRank() - 1) {
            Stack<Card> tmpStack = new Stack<>();
            int size = src.getBuildStack().size();
            for (int i = cardNum; i < size; i++) {
                tmpStack.push(src.getBuildStack().pop());
            }
            addStackToBuildStack(tmpStack, dst);
            src.revealCard();
            src.setBottomCard();
        }
        else {
            throw new InvalidMoveException("Invalid move: Cannot move partial build stack");
        }
    }

    private void addStackToBuildStack(Stack<Card> stack, Pile dst) {
        while (!stack.isEmpty()) {
            dst.addToBuildStack(stack.pop());
        }
    }

    private ArrayList<Card> getUsableCards() {
        ArrayList<Card> usableCards = new ArrayList<>();
        Card card = stock.getCard();

        while (!usableCards.contains(card)) {
            usableCards.add(card);
            card = stock.draw();;
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
                if (topCard.isBlack() != card.isBlack()) {
                    if (card.getRank() == topCard.getRank() - 1) {
                        possibleMoves.add(new Move(card));
                    }
                }
            }
            //Check eligibility for a pile move
        }
        return possibleMoves;
    }

    public boolean runGame() {
        boolean end = false;
        Scanner scanner = new Scanner(System.in);

        while (!end) {
            System.out.println(this + "\n");

            System.out.println("Select option:");
            System.out.println("1. Draw Cards from stock");
            System.out.println("2. Move card from stock to pile");
            System.out.println("3. Move card from stock to foundation");
            System.out.println("4. Move entire build stack");
            System.out.println("5. Move partial build stack");
            System.out.println("6. Move card from build stack to foundation");
            System.out.println("8. Exit");

            int choice = scanner.nextInt();

            try {
                if (choice == 1) {
                    this.stock.draw();
                }
                else if (choice == 2) {
                    System.out.println("Which pile are you moving the card to? (1-7) ");
                    int i = scanner.nextInt() - 1;
                    this.stockToPile(this.piles[i]);
                }
                else if (choice == 3) {
                    this.stockToFoundation();
                }
                else if (choice == 4) {
                    System.out.println("Which build stack would you like to move? (1-7)");
                    int x = scanner.nextInt() - 1;
                    System.out.println("Which pile would you like to move this stack to? (1-7) ");
                    int y = scanner.nextInt() - 1;
                    this.moveEntireBuildStack(this.piles[x], this.piles[y]);
                }
                else if (choice == 5) {
                    System.out.println("From which build stack would you like to move? (1-7)");
                    int x = scanner.nextInt() - 1;
                    System.out.println("Which pile would you like to move this stack to? (1-7) ");
                    int y = scanner.nextInt() - 1;
                    System.out.println("Which card in the build stack would you like to be the bottom of " +
                            "the moved stack? (1-" + this.piles[x].getBuildStack().size() + ")");
                    int z = scanner.nextInt() - 1;
                    this.movePartialBuildStack(this.piles[x], this.piles[y], z);
                }
                else if (choice == 6) {
                    System.out.println("From which build stack would you like to move? (1-7)");
                    int x = scanner.nextInt() - 1;
                    this.pileToFoundation(this.piles[x]);
                }
                else if (choice == 8) {
                    end = true;
                }
            }
            catch (InvalidMoveException e) {
                System.out.println(e.getMessage());
            }
            if (this.foundation.checkWin()) {
                System.out.println("You win");
                return true;
            }
        }
        System.out.println("You lose");
        return false;
    }

    public boolean solitaireSolver() {
        System.out.println(this + "\n");
        System.out.println("Stock: \t\t\t\t" + this.stock.stock);
        System.out.println("Usable Cards: \t\t" + this.getUsableCards());
        System.out.println("Possible Moves: \t" + this.getPossibleMoves());

        return true;
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
}