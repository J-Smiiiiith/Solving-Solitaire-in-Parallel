package SolitaireSolver;

import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;

public class Game {
    char[] suits;
    Stack<Card> deck;
    Pile[] piles;
    Foundation foundation;
    StockWaste stockWaste;

    private Game() {
        suits = new char[] {'C', 'S', 'H', 'D'};
        deck = buildDeck();
        this.shuffleDeck();
        piles = new Pile[] {new Pile(), new Pile(), new Pile(), new Pile(), new Pile(), new Pile(), new Pile()};
        foundation = new Foundation();

        this.buildDeck();
        this.dealCards();
        stockWaste = new StockWaste(deck);
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

    private void stockToPile(int pileIndex) {
        if (this.piles[pileIndex].addToBuildStack(this.stockWaste.getTopCard())) {
            this.stockWaste.removeTopCard();
        }
        else {
            System.out.println("Invalid move");
        }
    }

    private void stockToFoundation() {
        if (this.stockWaste.getTopCard().getSuit() == 'C') {
            if (this.stockWaste.getTopCard().getRank() == this.foundation.getClubs() - 1) {
                this.foundation.incrementClubs();
                this.stockWaste.removeTopCard();
            }
            else {
                System.out.println("Invalid move");
            }
        }
        else if (this.stockWaste.getTopCard().getSuit() == 'S') {
            if (this.stockWaste.getTopCard().getRank() == this.foundation.getSpades() - 1) {
                this.foundation.incrementSpades();
                this.stockWaste.removeTopCard();
            }
            else {
                System.out.println("Invalid move");
            }
        }
        else if (this.stockWaste.getTopCard().getSuit() == 'H') {
            if (this.stockWaste.getTopCard().getRank() == this.foundation.getHearts() - 1) {
                this.foundation.incrementHearts();
                this.stockWaste.removeTopCard();
            }
            else {
                System.out.println("Invalid move");
            }
        }
        else if (this.stockWaste.getTopCard().getSuit() == 'D') {
            if (this.stockWaste.getTopCard().getRank() == this.foundation.getDiamonds() - 1) {
                this.foundation.incrementDiamonds();
                this.stockWaste.removeTopCard();
            }
            else {
                System.out.println("Invalid move");
            }
        }
    }

    private void moveEntireBuildStack(Pile src, Pile dst) {
        if (src.getBottomCard().getRank() == dst.getTopCard().getRank() - 1) {
            Stack<Card> tmpStack = new Stack<>();
            while (!src.getBuildStack().isEmpty()) {
                tmpStack.push(src.getBuildStack().pop());
            }
            addStackToBuildStack(tmpStack, dst);
            src.revealCard();
            src.setBottomCard();
        }
        else {
            System.out.println("Invalid move");
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
    }

    private void addStackToBuildStack(Stack<Card> stack, Pile dst) {
        while (!stack.isEmpty()) {
            dst.addToBuildStack(stack.pop());
        }
    }

    @Override
    public String toString() {
        String output = "";

        output += stockWaste.getTopCard() + "\t\t\t" + foundation.getClubCard() + " " + foundation.getSpadeCard() + " " +
                foundation.getHeartCard() + " " + foundation.getDiamondCard() + "\n\n";

        for (Pile pile : piles) {
            output += pile.getPile() + "\n";
        }
        return output;
    }

    public static void main(String[] args) {
        Game game = new Game();

        boolean end = false;

        Scanner scanner = new Scanner(System.in);

        while (!end) {
            System.out.println(game + "\n");

            System.out.println("Select option:");
            System.out.println("1. Draw Cards from stock");
            System.out.println("2. Move card from stock to pile");
            System.out.println("3. Move card from stock to foundation");
            System.out.println("4. Move entire build stack");
            System.out.println("5. Move partial build stack");
            System.out.println("8. Exit");

            int choice = scanner.nextInt();
            if (choice == 1) {
                game.stockWaste.draw();
            }
            else if (choice == 2) {
                System.out.println("Which pile are you moving the card to? (1-7) ");
                int i = scanner.nextInt() - 1;

                game.stockToPile(i);
            }
            else if (choice == 3) {
                game.stockToFoundation();
            }
            else if (choice == 4) {
                System.out.println("Which build stack would you like to move? (1-7)");
                int x = scanner.nextInt() - 1;
                System.out.println("Which pile would you like to move this stack to? (1-7) ");
                int y = scanner.nextInt() - 1;

                game.moveEntireBuildStack(game.piles[x], game.piles[y]);
            }

            else if (choice == 5) {
                System.out.println("From which build stack would you like to move? (1-7)");
                int x = scanner.nextInt() - 1;
                System.out.println("Which pile would you like to move this stack to? (1-7) ");
                int y = scanner.nextInt() - 1;
                System.out.println("Which card in the build stack would you like to be the bottom of " +
                        "the moved stack? (1-" + game.piles[x].getBuildStack().size() + ")");
                int z = scanner.nextInt() - 1;

                game.movePartialBuildStack(game.piles[x], game.piles[y], z);
            }

            else if (choice == 8) {
                end = true;
            }

            if (game.stockWaste.getStockSize() == 0) {
                game.stockWaste.replenish();
            }
        }
    }
}