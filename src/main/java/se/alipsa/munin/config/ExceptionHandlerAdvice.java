package se.alipsa.munin.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import se.alipsa.munin.controller.ApiException;

/**
 * Global exception handler for the application.
 * Catches specific exceptions and returns appropriate HTTP responses.
 */
@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

  /**
   * Default constructor.
   */
  public ExceptionHandlerAdvice() {
    // Default constructor
  }

  /*
  @ExceptionHandler(Exception.class)
  public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public final ResponseEntity<Object> handleAllExceptions(ResponseStatusException ex, WebRequest request) {
    return new ResponseEntity<>(ex.getReason(), ex.getStatus());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public final ResponseEntity<Object> handleAllExceptions(IllegalArgumentException ex, WebRequest request) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

   */
  /**
   * Handles ApiException and returns a ResponseEntity with the exception message and status.
   *
   * @param ex      the ApiException to handle
   * @param request the current web request
   * @return a ResponseEntity containing the exception message and status
   */
  @ExceptionHandler(ApiException.class)
  public final ResponseEntity<Object> handleAllExceptions(ApiException ex, WebRequest request) {
    return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
  }
}
