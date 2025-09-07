package se.alipsa.munin.controller;

import org.springframework.http.HttpStatus;

/**
 * Custom exception class for API errors, encapsulating an HTTP status code and a message.
 */
public class ApiException extends RuntimeException {

  private final HttpStatus httpStatus;

  /**
   * Constructs a new ApiException with the specified HTTP status and message.
   *
   * @param status  the HTTP status to be associated with this exception
   * @param message the detail message
   */
  public ApiException(HttpStatus status, String message) {
    super(message);
    httpStatus = status;
  }

  /**
   * Returns the HTTP status associated with this exception.
   *
   * @return the HTTP status
   */
  public HttpStatus getStatus() {
    return httpStatus;
  }
}
