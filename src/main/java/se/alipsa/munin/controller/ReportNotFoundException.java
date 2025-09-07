package se.alipsa.munin.controller;

/**
 * Exception thrown when a requested report is not found.
 */
public class ReportNotFoundException extends Exception {

  /**
   * Constructs a new ReportNotFoundException with the specified detail message.
   *
   * @param message the detail message
   */
  public ReportNotFoundException(String message) {
    super(message);
  }

  /**
   * Constructs a new ReportNotFoundException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public ReportNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
