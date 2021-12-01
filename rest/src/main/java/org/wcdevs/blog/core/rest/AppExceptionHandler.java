package org.wcdevs.blog.core.rest;

import org.springframework.http.ResponseEntity;
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
  private final ErrorHandler errorHandler;

  public AppExceptionHandler(ErrorHandlerFactory errorHandlerFactory) {
    errorHandler = errorHandlerFactory.getHandler();
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorMessage> handleNotFound(Throwable e, WebRequest req) {
    return errorHandler.handle(e, req);
  }
}
