package SolitaireSolver;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Run {
    static int MAX_REPEATS = 5;
    static int RANDOMNESS_PERCENTAGE;
    static int NUM_SIMULATIONS;

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
            Move move = possibleMoves.get(randomInt);

            game.makeMove(move);
            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > MAX_REPEATS) {
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
            Move bestMove = game.getBestMoveWithPriority(possibleMoves);

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

    public static int greedyHeuristicPrioritySolitaireSolverWithRandom(Solitaire game) {
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;

        boolean end = false;
        while (!end) {
            possibleMoves = game.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                return game.getFoundation().getTotalFoundationCards();
            }

            int randInt = (int) (Math.random() * 100);
            if ((0 <= randInt) && (randInt < RANDOMNESS_PERCENTAGE)) {
                int randomInt = (int) (Math.random() * possibleMoves.size());
                game.makeMove(possibleMoves.get(randomInt));
            } else {
                Move bestMove = game.getBestMoveWithPriority(possibleMoves);
                game.makeMove(bestMove);
            }

            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > 3) {
                return game.getFoundation().getTotalFoundationCards();
            }
            if (game.getFoundation().checkWin()) {
                return 0;
            } else {
                gameStates.add(currentState);
            }
        }
        return -1;
    }

    public static boolean monteCarloSolitaireSolver(Solitaire game, int numSimulations) {
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;
        Stack<GameStateCopy> history = new Stack<>();

        boolean end = false;
        while (!end) {
            possibleMoves = game.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                return false;
            }

            for (Move move : possibleMoves) {
                history.push(new GameStateCopy(game));
                // Save game state before move
                game.makeMove(move);
                for (int i = 0; i < numSimulations; i++) {
                    history.push(new GameStateCopy(game));
                    // Save game state after move for simulation
                    int gameSim = greedyHeuristicPrioritySolitaireSolverWithRandom(game);
                    if (gameSim == 0) {
                        return true;
                        // If win found in simulation, this must be a winning configuration, no need to run more sims.
                    }
                    else {
                        move.setMonteCarloScore(gameSim);
                    }
                    history.pop().restoreGameState(game);
                    // Restore game to state after the simulated move was made
                }
                history.pop().restoreGameState(game);
                // Restore game to state before move was simulated
            }

            Move bestMove = game.getBestMoveMonetCarlo(possibleMoves);

            game.makeMove(bestMove);
            game.resetMonteCarloScores(possibleMoves);

            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > MAX_REPEATS) {
                return false;
            }
            if (game.getFoundation().checkWin() || game.checkWin()) {
                return true;
            } else {
                gameStates.add(currentState);
            }
        }
        return end;
    }

    public static void runSolver(int numRuns, int numThreads, char solverType) {
        int numTotalWins = 0;
        int numTotalThreadWins = 0;
        int totalRuns;
        ArrayList<Long> solverTimes = new ArrayList<>();
        for (totalRuns = 0; totalRuns < numRuns; totalRuns++) {
            long solverStart = System.nanoTime();

            AtomicInteger numWins = new AtomicInteger();
            Solitaire baseGame = new Solitaire(new Deck());
            System.out.println("Game " + (totalRuns + 1) + ": " + baseGame);

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            for (int j = 0; j < numThreads; j++) {
                executor.submit(() -> {
                    try {
                        boolean result;
                        Solitaire game = new Solitaire(baseGame);

                        result = switch (solverType) {
                            case 'r' -> randomSolitaireSolver(game);
                            case 'g' -> greedyHeuristicSolitaireSolver(game);
                            case 'p' -> greedyHeuristicPrioritySolitaireSolver(game);
                            case 'm' -> monteCarloSolitaireSolver(game, NUM_SIMULATIONS);
                            default -> false;
                        };
//                        System.out.println(Thread.currentThread().getName() + ": " + result);
                        if (result) {
                            numWins.getAndIncrement();
                        }
                    } catch (Exception e) {
                        System.err.println("[" + Thread.currentThread().getName() + "] encountered an error:");
                        e.printStackTrace();
                    }
                });
            }
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            long solverEnd = System.nanoTime();
            long durationMs = (solverEnd - solverStart) / 1_000_000;
            solverTimes.add(durationMs);
            int winsThisGame = numWins.get();

            System.out.println("Wins: " + winsThisGame + " out of " + numThreads + " games.");
            System.out.println("Time taken: " + durationMs + "ms\n");

            if (winsThisGame > 0) {
                numTotalWins++;
            }
            numTotalThreadWins += winsThisGame;
        }
        Collections.sort(solverTimes);
        long totalTime = 0;
        for (long time : solverTimes) {
            totalTime += time;
        }
        double meanTime = totalTime / solverTimes.size();
        long minTime = Collections.min(solverTimes);
        long maxTime = Collections.max(solverTimes);
        double medianTime;

        if (totalRuns % 2 == 0) {
            medianTime = (solverTimes.get((totalRuns / 2) - 1) + solverTimes.get(totalRuns / 2)) / 2;
        }
        else {
            medianTime = solverTimes.get(totalRuns / 2);
        }
        System.out.println("=======================================");
        System.out.println("Total wins:\t\t\t" + numTotalWins + "/" + (totalRuns));
        System.out.println("Solvability:\t\t" + (numTotalWins * 100.0 / totalRuns) + "%");
        System.out.println("---------------------------------------");
        if (numTotalWins > 0) {
            System.out.println("Mean thread wins per winning game:\t" + (double) (numTotalThreadWins / numTotalWins));
            System.out.println("---------------------------------------");
        }
        System.out.println("Mean time:\t\t\t" + meanTime + "ms");
        System.out.println("Median time:\t\t" + medianTime + "ms");
        System.out.println("Min time:\t\t\t" + minTime + "ms");
        System.out.println("Max time:\t\t\t" + maxTime + "ms");
        System.out.println("Total time:\t\t\t" + totalTime + "ms");
        System.out.println("---------------------------------------");
    }

    public static void main(String[] args) {
        int NUM_THREADS = 10;
        int NUM_RUNS = 10;
        char SOLVER_TYPE = 'm';
        RANDOMNESS_PERCENTAGE = 30;
        NUM_SIMULATIONS = 100;

        String solver = switch (SOLVER_TYPE) {
            case 'r' -> "Random Move Solver";
            case 'g' -> "Greedy Heuristic Solver";
            case 'p' -> "Greedy Heuristic Solver with Priority";
            case 'm' -> "Monte Carlo Solver";
            default -> "";
        };

        System.out.println("Solver: " + solver);
        System.out.println("Num threads: " + NUM_THREADS);
        System.out.println("Num runs: " + NUM_RUNS + "\n");

        runSolver(NUM_RUNS, NUM_THREADS, SOLVER_TYPE);
    }
}
