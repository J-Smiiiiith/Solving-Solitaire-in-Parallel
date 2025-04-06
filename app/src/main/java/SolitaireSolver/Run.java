package SolitaireSolver;

import java.util.ArrayList;
import java.util.Collections;

public class Run {

    public static boolean randomSolitaireSolver(Solitaire game) {
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;

        boolean end = false;
        while (!end) {
            possibleMoves = game.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                return false;
            }

            int randomInt = (int) (Math.random() * possibleMoves.size());
            game.makeMove(possibleMoves.get(randomInt));
            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > 3) {
                return false;
            }
            else if (game.getFoundation().checkWin()) {
                return true;
            } else {
                gameStates.add(currentState);
            }
        }
        return end;
    }

    public static boolean greedyHeuristicSolitaireSolver(Solitaire game) {
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;

        boolean end = false;
        while (!end) {
            possibleMoves = game.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                return false;
            }

            game.makeMove(game.getBestMove(possibleMoves));
            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > 3) {
                return false;
            }
            if (game.getFoundation().checkWin()) {
                return true;
            } else {
                gameStates.add(currentState);
            }
        }
        return end;
    }

    public static boolean greedyHeuristicPrioritySolitaireSolver(Solitaire game) {
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;

        boolean end = false;
        while (!end) {
            possibleMoves = game.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                return false;
            }

            game.makeMove(game.getBestMoveWithPriority(possibleMoves));
            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > 3) {
                return false;
            }
            if (game.getFoundation().checkWin()) {
                return true;
            } else {
                gameStates.add(currentState);
            }
        }
        return end;
    }

    public static void runSolver(int numRuns, char solverType) {
        int numGames = 0;
        int numWins = 0;
        for (int i = 0; i < numRuns; i++) {
            Solitaire game = new Solitaire();
            switch (solverType) {
                case 'p':
                    if (greedyHeuristicPrioritySolitaireSolver(game)) {
                        numWins++;
                    }
                    break;
                case 'g':
                    if (greedyHeuristicSolitaireSolver(game)) {
                        numWins++;
                    }
                    break;
                case 'r':
                    if (randomSolitaireSolver(game)) {
                        numWins++;
                    }
                    break;
            }
            numGames++;
        }
        System.out.println("\nWon " + numWins + "/" + numGames + " games.");
    }

    public static void main(String[] args) {
        runSolver(10000, 'p');
    }
}
