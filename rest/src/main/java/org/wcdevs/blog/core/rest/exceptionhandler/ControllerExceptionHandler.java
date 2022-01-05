package org.wcdevs.blog.core.rest.exceptionhandler;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles exceptions thrown by the business logic.
 */
@RestControllerAdvice
public class ControllerExceptionHandler {
  private final AppExceptionHandler chainedAppExceptionHandler;

  public ControllerExceptionHandler(ExceptionHandlerFactory exceptionHandlerFactory) {
    chainedAppExceptionHandler = exceptionHandlerFactory.getChainedHandler();
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorMessage> handleNotFound(Throwable e, WebRequest req) {
    return chainedAppExceptionHandler.handle(e, req);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorMessage> handleNoReadableException(HttpMessageNotReadableException e) {
    throw e;
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorMessage> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e, WebRequest request) {
    var error = new ErrorMessage(e.getMessage(), request.getContextPath(), LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
  }
}
