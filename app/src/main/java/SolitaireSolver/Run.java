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

    public static boolean greedyHeuristicPrioritySolitaireSolverWithRandom(Solitaire game) {
        //System.out.println("\nNew simulation: \n");
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;

        boolean end = false;
        while (!end) {
            //System.out.println("Current game state:\n" + game);
            possibleMoves = game.getPossibleMoves();
            //System.out.println("Possible moves: " + possibleMoves);
            if (possibleMoves.isEmpty()) {
                //System.out.println("No possible moves left.");
                return false;
            }

            if (((int) (Math.random() * 3)) == 0) {
                int randomInt = (int) (Math.random() * possibleMoves.size());
                //System.out.println("Random move selected: " + possibleMoves.get(randomInt));
                game.makeMove(possibleMoves.get(randomInt));
            } else {
                Move bestMove = game.getBestMoveWithPriority(possibleMoves);
                //System.out.println("Best move selected: " + bestMove);
                game.makeMove(bestMove);
            }

            String currentState = game.getGameState();
            //System.out.println("New game state: " + currentState);

            if (Collections.frequency(gameStates, currentState) > 3) {
                //System.out.println("Game state repeated more than 3 times.");
                return false;
            }
            if (game.getFoundation().checkWin()) {
                //System.out.println("Game won!");
                return true;
            } else {
                gameStates.add(currentState);
            }
        }
        return end;
    }

    public static boolean monteCarloSolitaireSolver(Solitaire game, int numSimulations) {
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;

        boolean end = false;
        while (!end) {
            //System.out.println(game);
            possibleMoves = game.getPossibleMoves();
            //System.out.println("Possible moves: " + possibleMoves);
            if (possibleMoves.isEmpty()) {
                System.out.println("No possible moves left.");
                return false;
            }

            for (Move move : possibleMoves) {
                move.resetMonteCarloScore();
                int successCount = 0;
                for (int i = 0; i < numSimulations; i++) {
                    Solitaire simulatedGame = new Solitaire(game);
                    if (greedyHeuristicPrioritySolitaireSolverWithRandom(simulatedGame)) {
                        move.incrementMonteCarloScore();
                        successCount++;
                    }
                }
                System.out.println("Move: " + move + " won " + successCount + "/" + numSimulations);
                //System.out.println("Move: " + move + ", Monte Carlo score: " + move.getMonteCarloScore());
            }

            Move bestMove = game.getBestMoveMonetCarlo(possibleMoves);
            //System.out.println("Best move selected: " + bestMove);
            game.makeMove(bestMove);
            String currentState = game.getGameState();
            //System.out.println("Current game state: " + currentState);

            if (Collections.frequency(gameStates, currentState) > 3) {
                System.out.println("Game state repeated more than 3 times.");
                return false;
            }
            if (game.getFoundation().checkWin()) {
                System.out.println("Game won!");
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
                case 'm':
                    if (monteCarloSolitaireSolver(game, 25)) {
                        numWins++;
                    }
                    break;
            }
            numGames++;
        }
        System.out.println("\nWon " + numWins + "/" + numGames + " games.");
    }

    public static void main(String[] args) {
        runSolver(1000, 'm');
    }
}
