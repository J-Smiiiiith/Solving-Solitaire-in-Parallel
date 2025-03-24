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
        //Stock to pile
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
            if (game.stockWaste.getStockSize() == 0) {
                game.stockWaste.replenish();
            }

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

                //Stock to foundation
                if (game.stockWaste.getTopCard().getSuit() == 'C') {
                    if (game.stockWaste.getTopCard().getRank() == game.foundation.getClubs() - 1) {
                        game.foundation.incrementClubs();
                        game.stockWaste.removeTopCard();
                    }
                    else {
                        System.out.println("Invalid move");
                    }
                }
                else if (game.stockWaste.getTopCard().getSuit() == 'S') {
                    if (game.stockWaste.getTopCard().getRank() == game.foundation.getSpades() - 1) {
                        game.foundation.incrementSpades();
                        game.stockWaste.removeTopCard();
                    }
                    else {
                        System.out.println("Invalid move");
                    }
                }
                else if (game.stockWaste.getTopCard().getSuit() == 'H') {
                    if (game.stockWaste.getTopCard().getRank() == game.foundation.getHearts() - 1) {
                        game.foundation.incrementHearts();
                        game.stockWaste.removeTopCard();
                    }
                    else {
                        System.out.println("Invalid move");
                    }
                }
                else if (game.stockWaste.getTopCard().getSuit() == 'D') {
                    if (game.stockWaste.getTopCard().getRank() == game.foundation.getDiamonds() - 1) {
                        game.foundation.incrementDiamonds();
                        game.stockWaste.removeTopCard();
                    }
                    else {
                        System.out.println("Invalid move");
                    }
                }
                //Stock to foundation
            }
            else if (choice == 4) {
                System.out.println("Which build stack would you like to move? (1-7)");
                int x = scanner.nextInt() - 1;
                System.out.println("Which pile would you like to move this stack to? (1-7) ");
                int y = scanner.nextInt() - 1;

                //Move entire build stack
                if (game.piles[x].getBottomCard().getRank() == game.piles[y].getTopCard().getRank() - 1) {
                    Stack<Card> tmpStack = new Stack<>();
                    int size = game.piles[x].getBuildStack().size();
                    for (int i = 0; i < size; i++) {
                        tmpStack.push(game.piles[x].getBuildStack().pop());
                    }

                    size = tmpStack.size();
                    for (int i = 0; i < size; i++) {
                        game.piles[y].addToBuildStack(tmpStack.pop());
                    }
                    game.piles[x].revealCard();
                    game.piles[x].setBottomCard();
                }
                else {
                    System.out.println("Invalid move");
                }
                //Move entire build stack
            }

            else if (choice == 5) {
                System.out.println("From which build stack would you like to move? (1-7)");
                int x = scanner.nextInt() - 1;
                System.out.println("Which pile would you like to move this stack to? (1-7) ");
                int y = scanner.nextInt() - 1;
                System.out.println("Which card in the build stack would you like to be the bottom of " +
                        "the moved stack? (1-" + game.piles[x].getBuildStack().size() + ")");
                int z = scanner.nextInt() - 1;

                // Move partial build stack
                if (game.piles[x].getBuildCard(z).getRank() == game.piles[y].getTopCard().getRank() - 1) {
                    Stack<Card> tmpStack = new Stack<>();
                    int size = game.piles[x].getBuildStack().size();
                    for (int i = z; i < size; i++) {
                        tmpStack.push(game.piles[x].getBuildStack().pop());
                    }

                    size = tmpStack.size();
                    for (int i = 0; i < size; i++) {
                        game.piles[y].addToBuildStack(tmpStack.pop());
                    }
                    game.piles[x].revealCard();
                    game.piles[x].setBottomCard();
                }
                else {
                    System.out.println("Invalid move");
                }
                // Move partial build stack
                // Very similar to full build stack move, could be modularised?
            }

            else if (choice == 8) {
                end = true;
            }
        }
    }
}