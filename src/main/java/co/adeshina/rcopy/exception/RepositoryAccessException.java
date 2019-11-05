package co.adeshina.rcopy.exception;

/**
 * Should be thrown for any exceptions that prevent access to a remote Git repository.
 */
public class RepositoryAccessException extends Exception {

    public RepositoryAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
