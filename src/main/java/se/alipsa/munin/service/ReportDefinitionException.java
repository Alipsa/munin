package se.alipsa.munin.service;

public class ReportDefinitionException extends Exception {

  public ReportDefinitionException(String message) {
    super(message);
  }

  public ReportDefinitionException(String message, Throwable cause) {
    super(message, cause);
  }
}
