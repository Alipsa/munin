package se.alipsa.munin.service;

public class ReportEngineRuntimeException extends RuntimeException {

  public ReportEngineRuntimeException(String message) {
    super(message);
  }

  public ReportEngineRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
