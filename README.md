# Solving Solitaire in Parallel

This project was created in part fulfilment for the degree of BEng in Computer Science. The full report for this project can be found [here](Solving_Solitaire_Klondike_in_Parallel.pdf).

This project contains 4 different solver types to solve a game of Solitaire:
* Random Move Solver:
    * The move made at any given point in the game is randomly selected.
* Greedy Heuristic Solver:
    * Possibile moves are assigned a heuristic score.
    * The move with the highest score is made.
* Greedy Heuristic Solver with Priority:
    * A priority is assinged to moves.
    * If moves have the same heuristic score, the move with the highest priority is selected.
* Monte Carlo Solver:
    * Monte Carlo rollouts are used to determine which move is made.
    * For each possible move, the rest of the game is simulated.
        * An R-Greedy heuristic algorithm is used to decide which moves are made in the simulated games.
        * There is an R% chance that the selected move will be chosen at random.
        * Otherwise the score with the highest heuristc score and priority is selected.
    * The results of the simulations will update a Monte Carlo score for each move.
    * The move with the highest Monte Carlo score is selected. 

 ### Running the solvers:
 The solvers are ran using the Run.java file. Type the following into the terminal:

 Navigate to the correct folder:
```
cd app/src/main/java/
```

Compile the Java program:
```
javac SolitaireSolver/*.java SolitaireSolver/Exceptions/*java
```

Running the Random move or Greedy Heuristic solvers:
```
java SolitaireSolver.Run <numThreads> <numRuns> <solverType>
```

Running the Monte Carlo solver:
```
java SolitaireSolver.Run <numThreads> <numRuns> <solverType> <randomnessPercentage> <numSimulations>
```
 
 ### Parameters used run the solvers:

The parameters that need to be passed into Run.main() are as follows:
* `numThreads`:
    * `int`: The number of times you want the same game of solitaire to be ran through the solver.
    * Must be > 0.
* `numRuns`:
    * `int`: The number of different games you want to be solved by the solver.
    * Must be > 0.
* `solverType`:
    * `char`: Represents which solver you want to run:
        * 'r' = Random move solver
        * 'g' = Greedy Heuristic solver
        * 'p' = Greedy Heuristic Solver with Priority
        * 'm' = Monte Carlo Solver
          
The parameters below are only required if `solverType` = 'm':
* `randomnessPercentage`:
    * `int`: The percentage chance a move made in a simulation will be randomly selected.
    * Must be between 0 and 100 (inclusive).
* `numSimulations`:
    * `int`: The number of game simulations carried out on each potential move.
    * Must be > 0.
 
