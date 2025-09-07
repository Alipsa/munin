package se.alipsa.munin.controller;

/**
 * Exception thrown when adding a new user fails.
 */
public class AddUserException extends Exception {

  /**
   * Constructs a new AddUserException with the specified detail message.
   *
   * @param message the detail message
   */
  public AddUserException(String message) {
    super(message);
  }

  /**
   * Constructs a new AddUserException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public AddUserException(String message, Throwable cause) {
    super(message, cause);
  }
}
