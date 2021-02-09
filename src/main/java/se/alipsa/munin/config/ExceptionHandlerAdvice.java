package se.alipsa.munin.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import se.alipsa.munin.controller.ApiException;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

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
  @ExceptionHandler(ApiException.class)
  public final ResponseEntity<Object> handleAllExceptions(ApiException ex, WebRequest request) {
    return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
  }
}
