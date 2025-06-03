package hr.java.payroll.exception;

/**
 * Represents an unchecked exception related to database operations.
 * This exception is thrown when there is an issue with database access or execution.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class DatabaseException extends RuntimeException {

  /** Constructs a new DatabaseException with no detail message. */
  public DatabaseException() {}

  /**
   * Constructs a new DatabaseException with the specified detail message.
   *
   * @param message the detail message
   */
  public DatabaseException(String message) {
    super(message);
  }

  /**
   * Constructs a new DatabaseException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public DatabaseException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new DatabaseException with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public DatabaseException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new DatabaseException with the specified details.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   * @param enableSuppression whether suppression is enabled or disabled
   * @param writableStackTrace whether the stack trace should be writable
   */
  public DatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
