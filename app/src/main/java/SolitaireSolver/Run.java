package SolitaireSolver;

public class Run {

    public static void randomGame() {
        int numGames = 0;
        int numWins = 0;
        int numRuns = 1000;
        for (int i = 0; i < numRuns; i++) {
            Solitaire game = new Solitaire();
            if (game.randomSolitaireSolver()) {
                numWins++;
            }
            numGames++;

        }
        System.out.println("\nWon " + numWins + "/" + numGames + " games.");
    }

    public static void main(String[] args) {
        randomGame();
    }
}
