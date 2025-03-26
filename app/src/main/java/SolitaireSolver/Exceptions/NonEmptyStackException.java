package SolitaireSolver.Exceptions;

public class NonEmptyStackException extends RuntimeException {
    public NonEmptyStackException(String message) {
        super(message);
    }
}
