package org.wcdevs.blog.core.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.wcdevs.blog.core.common.exception.NotFoundException;
import org.wcdevs.blog.core.rest.util.ErrorMessage;

/**
 * Handles exceptions thrown by the business logic.
 */
@RestControllerAdvice
public class AppExceptionHandler {
  /**
   * Handles {@link NotFoundException}s.
   *
   * @param e   Exception
   * @param req Web request
   *
   * @return A response entity with info about the exception.
   */
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorMessage> handleNotFound(NotFoundException e, WebRequest req) {
    return new ResponseEntity<>(ErrorMessage.from(e, req), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorMessage> handleViolation(DataIntegrityViolationException violation,
                                                      WebRequest request) {
    return new ResponseEntity<>(ErrorMessage.from(violation, request), HttpStatus.CONFLICT);
  }
}
