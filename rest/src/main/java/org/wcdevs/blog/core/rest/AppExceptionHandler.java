package org.wcdevs.blog.core.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.wcdevs.blog.core.rest.errorhandler.ErrorHandler;
import org.wcdevs.blog.core.rest.errorhandler.ErrorHandlerFactory;
import org.wcdevs.blog.core.rest.errorhandler.ErrorMessage;

/**
 * Handles exceptions thrown by the business logic.
 */
@RestControllerAdvice
public class AppExceptionHandler {
  private final ErrorHandler chainedErrorHandler;

  public AppExceptionHandler(ErrorHandlerFactory errorHandlerFactory) {
    chainedErrorHandler = errorHandlerFactory.getChainedHandler();
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorMessage> handleNotFound(Throwable e, WebRequest req) {
    return chainedErrorHandler.handle(e, req);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorMessage> handleNotFound(HttpMessageNotReadableException e) {
    throw e;
  }
}
