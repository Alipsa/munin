package se.alipsa.munin.service;

/**
 * Exception thrown when there is an issue with report definitions.
 */
public class ReportDefinitionException extends Exception {

  /**
   * Constructs a new ReportDefinitionException with the specified detail message.
   *
   * @param message the detail message
   */
  public ReportDefinitionException(String message) {
    super(message);
  }

  /**
   * Constructs a new ReportDefinitionException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public ReportDefinitionException(String message, Throwable cause) {
    super(message, cause);
  }
}
