package gaarason.database.exception;

public class SQLRuntimeException extends RuntimeException {

    public SQLRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
