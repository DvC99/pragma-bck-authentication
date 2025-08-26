package co.com.crediya.model.exceptions;

public class RepositoryException extends InfrastructureException {
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
