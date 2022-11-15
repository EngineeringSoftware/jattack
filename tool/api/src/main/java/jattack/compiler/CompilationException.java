package jattack.compiler;

/**
 * Exception thrown when in-memory compilation fails.
 */
public class CompilationException extends Exception {

    public CompilationException(String message) {
        super(message);
    }
}
