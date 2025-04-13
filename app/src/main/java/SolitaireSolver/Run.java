package SolitaireSolver;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Run {
    static int MAX_REPEATS = 5;

    private static Stack<GameStateCopy> history = new Stack<>();

    public static void outputGame(Solitaire game, ArrayList<Move> moves, Move chosenMove) {
        String output = "";

        output += game + "\n" +
                "Possible moves: " + moves + "\n" +
                "Chosen move: " + chosenMove + "\n";

        System.out.println(output);
    }

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
                //System.out.println("No possible moves left.");
                return false;
            }
            Move bestMove = game.getBestMoveWithPriority(possibleMoves);

            //outputGame(game, possibleMoves, bestMove);

            game.makeMove(bestMove);
            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > MAX_REPEATS) {
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
//                System.out.println("No possible moves left.");
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
//                System.out.println("Game state repeated more than 3 times.");
                return false;
            }
            if (game.getFoundation().checkWin()) {
//                System.out.println("Game won!");
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

            // Save game state before move
            for (Move move : possibleMoves) {
                history.push(new GameStateCopy(game));
                game.makeMove(move);
                int successCount = 0;
                // Save game state after move for simulation
                for (int i = 0; i < numSimulations; i++) {
                    history.push(new GameStateCopy(game));
                    if (greedyHeuristicPrioritySolitaireSolverWithRandom(game)) {
                        successCount++;
                        move.incrementMonteCarloScore();
                    }
                    move.resetMonteCarloScore();
                    history.pop().restoreGameState(game);
                    // Restore game to state after the simulated move was made
                }
                history.pop().restoreGameState(game);
                // Restore game to state before move was simulated
                //System.out.println("Move: " + move + " won " + successCount + "/" + numSimulations);
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
                    if (monteCarloSolitaireSolver(game, 50)) {
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
