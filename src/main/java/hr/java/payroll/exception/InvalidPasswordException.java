package hr.java.payroll.exception;

/**
 * Represents a checked exception thrown when an invalid password is encountered.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class InvalidPasswordException extends Exception {

  /** Constructs a new InvalidPasswordException with no detail message. */
  public InvalidPasswordException() {
  }

  /**
   * Constructs a new InvalidPasswordException with the specified detail message.
   *
   * @param message the detail message
   */
  public InvalidPasswordException(String message) {
    super(message);
  }

  /**
   * Constructs a new InvalidPasswordException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public InvalidPasswordException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new InvalidPasswordException with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public InvalidPasswordException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new InvalidPasswordException with the specified details.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   * @param enableSuppression whether suppression is enabled or disabled
   * @param writableStackTrace whether the stack trace should be writable
   */
  public InvalidPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
