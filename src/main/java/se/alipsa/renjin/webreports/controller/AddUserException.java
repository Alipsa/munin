package se.alipsa.renjin.webreports.controller;

public class AddUserException extends Exception {

  public AddUserException(String message) {
    super(message);
  }

  public AddUserException(String message, Throwable cause) {
    super(message, cause);
  }
}
