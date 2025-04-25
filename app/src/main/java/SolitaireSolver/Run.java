package SolitaireSolver;

import java.util.*;
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

    public static int randomSolitaireSolver(Solitaire game) {
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;
        int movesMade = 0;

        boolean end = false;
        while (!end) {
            possibleMoves = game.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                return -movesMade;
            }

            int randomInt = (int) (Math.random() * possibleMoves.size());
            Move move = possibleMoves.get(randomInt);

            game.makeMove(move);
            movesMade++;
            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > MAX_REPEATS) {
                return -movesMade;
            }
            else if (game.getFoundation().checkWin()) {
                return movesMade;
            } else {
                gameStates.add(currentState);
            }
        }
        return -movesMade;
    }

    public static int greedyHeuristicSolitaireSolver(Solitaire game) {
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;
        int movesMade = 0;

        boolean end = false;
        while (!end) {
            possibleMoves = game.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                return -movesMade;
            }

            game.makeMove(game.getBestMove(possibleMoves));
            movesMade++;
            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > MAX_REPEATS) {
                return -movesMade;
            }
            if (game.getFoundation().checkWin()) {
                return movesMade;
            } else {
                gameStates.add(currentState);
            }
        }
        return -movesMade;
    }

    public static int greedyHeuristicPrioritySolitaireSolver(Solitaire game) {
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;
        int movesMade = 0;

        boolean end = false;
        while (!end) {
            possibleMoves = game.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                return -movesMade;
            }
            Move bestMove = game.getBestMoveWithPriority(possibleMoves);

            game.makeMove(bestMove);
            movesMade++;
            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > MAX_REPEATS) {
                return -movesMade;
            }
            if (game.getFoundation().checkWin()) {
                return movesMade;
            } else {
                gameStates.add(currentState);
            }
        }
        return -movesMade;
    }

    public static int greedyHeuristicPrioritySolitaireSolverWithRandom(Solitaire game) {
        //Returns the number of visible cards remaining in the game.
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;

        boolean end = false;
        while (!end) {
            possibleMoves = game.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                return 52 - game.getHiddenCardsCount();
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
                return 52 - game.getHiddenCardsCount();
            }
            if (game.getFoundation().checkWin()) {
                return 52;
            } else {
                gameStates.add(currentState);
            }
        }
        return -1;
    }

    public static int monteCarloSolitaireSolver(Solitaire game, int numSimulations) {
        ArrayList<String> gameStates = new ArrayList<>();
        gameStates.add(game.getGameState());
        ArrayList<Move> possibleMoves;
        Stack<GameStateCopy> history = new Stack<>();
        int movesMade = 0;

        boolean end = false;
        while (!end) {
            possibleMoves = game.getPossibleMoves();
            if (possibleMoves.isEmpty()) {
                return -movesMade;
            }

            for (Move move : possibleMoves) {
                history.push(new GameStateCopy(game));
                // Save game state before move
                game.makeMove(move);
                for (int i = 0; i < numSimulations; i++) {
                    history.push(new GameStateCopy(game));
                    // Save game state after move for simulation
                    int gameSim = greedyHeuristicPrioritySolitaireSolverWithRandom(game);
                    if (gameSim == 52) {
                        return movesMade + 1;
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
            movesMade++;
            game.resetMonteCarloScores(possibleMoves);

            String currentState = game.getGameState();

            if (Collections.frequency(gameStates, currentState) > MAX_REPEATS) {
                return -movesMade;
            }
            if (game.getFoundation().checkWin() || game.checkWin()) {
                return movesMade;
            } else {
                gameStates.add(currentState);
            }
        }
        return -movesMade;
    }

    public static void runSolver(int numRuns, int numThreads, char solverType) {
        int numTotalWins = 0;
        int numTotalThreadWins = 0;
        int totalRuns;
        ArrayList<Double> solverTimes = new ArrayList<>();
        ArrayList<Double> solverTimesWins = new ArrayList<>();
        ArrayList<Double> solverTimesLosses = new ArrayList<>();

        List<Double> wonGamesMoves = Collections.synchronizedList(new ArrayList<>());
        List<Double> lostGameMoves = Collections.synchronizedList(new ArrayList<>());

        for (totalRuns = 0; totalRuns < numRuns; totalRuns++) {
            long solverStart = System.nanoTime();

            AtomicInteger numWins = new AtomicInteger();
            Solitaire baseGame = new Solitaire(new Deck());
            System.out.println("Game " + (totalRuns + 1) + ": " + baseGame);

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            for (int j = 0; j < numThreads; j++) {
                executor.submit(() -> {
                    try {
                        int result;
                        Solitaire game = new Solitaire(baseGame);

                        result = switch (solverType) {
                            case 'r' -> randomSolitaireSolver(game);
                            case 'g' -> greedyHeuristicSolitaireSolver(game);
                            case 'p' -> greedyHeuristicPrioritySolitaireSolver(game);
                            case 'm' -> monteCarloSolitaireSolver(game, NUM_SIMULATIONS);
                            default -> -1;
                        };
//                        System.out.println(Thread.currentThread().getName() + ": " + result);
                        if (result > 0) {
                            numWins.getAndIncrement();
                            wonGamesMoves.add((double) result);
                        }
                        else {
                            lostGameMoves.add((double) -result);
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
            double durationMs = Math.round((float) (solverEnd - solverStart) / 1_000_000);
            solverTimes.add(durationMs);
            int winsThisGame = numWins.get();

            System.out.println("Wins: " + winsThisGame + " out of " + numThreads + " games.");
            System.out.println("Time taken: " + durationMs + "ms\n");

            if (winsThisGame > 0) {
                numTotalWins++;
                solverTimesWins.add(durationMs);
            }
            else {
                solverTimesLosses.add(durationMs);
            }
            numTotalThreadWins += winsThisGame;
        }

        System.out.println(wonGamesMoves);
        System.out.println(lostGameMoves);

        Map<String, Double> allAverages = getTimeAverages(solverTimes);
        Map<String, Double> winAverages = getTimeAverages(solverTimesWins);
        Map<String, Double> lossAverages = getTimeAverages(solverTimesLosses);
        Map<String, Double> avMovesOnWinThreads = getMoveAverages(wonGamesMoves);
        Map<String, Double> avMovesOnLossThreads = getMoveAverages(lostGameMoves);

        System.out.println("========================================");
        System.out.println("Total wins:\t\t\t" + numTotalWins + "/" + (totalRuns));
        System.out.println("Solvability:\t\t" + (numTotalWins * 100.0 / totalRuns) + "%");
        System.out.println("----------------------------------------");
        if (numTotalWins > 0) {
            System.out.println("Mean thread wins per winning game:\t" + (double) (numTotalThreadWins / numTotalWins));
            System.out.println("----------------------------------------");
        }
        System.out.println("Timings:");
        System.out.println("----------------------------------------");
        System.out.println("Wins:\t\t\t" + numTotalWins);
        outputTimeAverages(winAverages);
        System.out.println("----------------------------------------");

        System.out.println("Losses:\t\t\t" + (totalRuns - numTotalWins));
        outputTimeAverages(lossAverages);
        System.out.println("----------------------------------------");

        System.out.println("All games:\t\t" + totalRuns);
        outputTimeAverages(allAverages);
        System.out.println("----------------------------------------");

        System.out.println("Move averages in threaded games:");
        System.out.println("----------------------------------------");
        System.out.println("Wins:\t\t\t" + numTotalWins);
        outputMoveAverages(avMovesOnWinThreads);
        System.out.println("----------------------------------------");
        System.out.println("Losses:\t\t\t" + (totalRuns - numTotalWins));
        outputMoveAverages(avMovesOnLossThreads);
        System.out.println("----------------------------------------");
    }

    private static void outputTimeAverages(Map<String, Double> averages) {
        System.out.println("Mean:\t\t\t" + averages.get("Mean") + "ms");
        System.out.println("Median:\t\t\t" + averages.get("Median") + "ms");
        System.out.println("Min:\t\t\t" + averages.get("Min") + "ms");
        System.out.println("Max:\t\t\t" + averages.get("Max") + "ms");
        System.out.println("Total:\t\t\t" + averages.get("Total") + "ms");
    }

    private static void outputMoveAverages(Map<String, Double> averages) {
        System.out.println("Mean:\t\t\t" + averages.get("Mean") + " moves");
        System.out.println("Median:\t\t\t" + averages.get("Median") + " moves");
        System.out.println("Mode:\t\t\t" + averages.get("Mode") + " moves (" + averages.get("Mode Count") + ")");
        System.out.println("Min:\t\t\t" + averages.get("Min") + " moves");
        System.out.println("Max:\t\t\t" + averages.get("Max") + " moves");
        System.out.println("Total:\t\t\t" + averages.get("Total") + " moves");
    }

    public static Map<String, Double> getTimeAverages(ArrayList<Double> times) {
        int totalRuns = times.size();
        Map<String, Double> averages = new HashMap<>();

        if (totalRuns == 0) {
            return averages;
        }

        double totalTime = 0;
        for (double time : times) {
            totalTime += time;
        }

        averages.put("Total", totalTime);

        averages.put("Mean", Math.round(totalTime / totalRuns * 100.0) / 100.0);
        averages.put("Max", Collections.max(times));
        averages.put("Min", Collections.min(times));

        Collections.sort(times);
        if (totalRuns % 2 == 0) {
            double mid1 = times.get((totalRuns / 2) - 1);
            double mid2 = times.get(totalRuns / 2);
            double median = (mid1 + mid2) / 2.0;
            averages.put("Median", Math.round(median * 100.0) / 100.0);
        }
        else {
            double median = times.get(totalRuns / 2);
            averages.put("Median", Math.round(median * 100.0) / 100.0);
        }
        return averages;
    }

    public static Map<String, Double> getMoveAverages(List<Double> moves) {
        int totalRuns = moves.size();
        Map<Double, Integer> modeMap = new HashMap<>();
        Map<String, Double> averages = new HashMap<>();

        if (totalRuns == 0) {
            return averages;
        }

        double totalMoves = 0;
        for (double move : moves) {
            totalMoves += move;

            if (!modeMap.containsKey(move)) {
                modeMap.put(move, 1);
            } else {
                modeMap.put(move, (modeMap.get(move) + 1));
            }
        }

        Double mode = null;
        int maxCount = 0;

        for (Map.Entry<Double, Integer> entry : modeMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mode = entry.getKey();
            }
        }
        averages.put("Mode", mode);
        averages.put("Mode Count", (double) maxCount);
        averages.put("Total", totalMoves);

        averages.put("Mean", Math.round(totalMoves / totalRuns * 100.0) / 100.0);
        averages.put("Max", Collections.max(moves));
        averages.put("Min", Collections.min(moves));

        Collections.sort(moves);
        if (totalRuns % 2 == 0) {
            double mid1 = moves.get((totalRuns / 2) - 1);
            double mid2 = moves.get(totalRuns / 2);
            double median = (mid1 + mid2) / 2.0;
            averages.put("Median", Math.round(median * 100.0) / 100.0);
        }
        else {
            double median = moves.get(totalRuns / 2);
            averages.put("Median", Math.round(median * 100.0) / 100.0);
        }
        return averages;
    }

    public static void main(String[] args) {
        int NUM_THREADS = 1;
        int NUM_RUNS = 100;
        char SOLVER_TYPE = 'm';
        RANDOMNESS_PERCENTAGE = 30;
        NUM_SIMULATIONS = 25;

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
