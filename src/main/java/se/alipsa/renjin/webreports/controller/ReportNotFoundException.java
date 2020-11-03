package se.alipsa.renjin.webreports.controller;

public class ReportNotFoundException extends Exception {
  public ReportNotFoundException(String message) {
    super(message);
  }

  public ReportNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
