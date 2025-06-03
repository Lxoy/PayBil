package hr.java.payroll.exception;

/**
 * Represents an unchecked exception thrown when an invalid input is encountered.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class InvalidInputException extends RuntimeException {
    /** Constructs a new InvalidInputException with no detail message. */
    public InvalidInputException() {}

    /**
     * Constructs a new InvalidInputException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidInputException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidInputException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public InvalidInputException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new InvalidInputException with the specified details.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param enableSuppression whether suppression is enabled or disabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    public InvalidInputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
