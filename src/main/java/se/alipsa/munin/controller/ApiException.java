package se.alipsa.munin.controller;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

  HttpStatus httpStatus;

  public ApiException(HttpStatus status, String message) {
    super(message);
    httpStatus = status;
  }

  public HttpStatus getStatus() {
    return httpStatus;
  }
}
