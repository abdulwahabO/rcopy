package co.adeshina.rcopy.exception;

/**
 * Thrown for any exceptions that prevent access to a hosted Git repository.
 */
public class RepositoryAccessException extends Exception {

    public RepositoryAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryAccessException(String message) {
        super(message);
    }
}
