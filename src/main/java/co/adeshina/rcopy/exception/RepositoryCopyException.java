package co.adeshina.rcopy.exception;

/**
 * Thrown if the process of copying the contents of a remote repository fails.
 */
public class RepositoryCopyException extends Exception {

    public RepositoryCopyException(String message, Throwable cause) {
        super(message, cause);
    }

}
