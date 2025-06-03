package hr.java.payroll.exception;

/**
 * Represents an unchecked exception thrown when a required field is missing.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class MissingRequiredFieldException extends RuntimeException {
    /** Constructs a new MissingRequiredFieldException with no detail message. */
    public MissingRequiredFieldException() {
    }

    /**
     * Constructs a new MissingRequiredFieldException with the specified detail message.
     *
     * @param message the detail message
     */
    public MissingRequiredFieldException(String message) {
        super(message);
    }

    /**
     * Constructs a new MissingRequiredFieldException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public MissingRequiredFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new MissingRequiredFieldException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public MissingRequiredFieldException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new MissingRequiredFieldException with the specified details.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param enableSuppression whether suppression is enabled or disabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    public MissingRequiredFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
