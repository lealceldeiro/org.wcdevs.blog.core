package com.wcdevs.blog.core.rest;

import com.wcdevs.blog.core.common.exception.NotFoundException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles exceptions thrown by the business logic.
 */
@RestControllerAdvice
public class AppExceptionHandler {
  static class ErrorMessage {
    public final String message;
    public final String context;
    public final LocalDateTime dateTime;

    private ErrorMessage(String message, String context, LocalDateTime dateTime) {
      this.message = message;
      this.context = context;
      this.dateTime = dateTime;
    }
  }

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
    ErrorMessage errorMsg = new ErrorMessage(e.getMessage(), req.getContextPath(),
                                             LocalDateTime.now());
    return new ResponseEntity<>(errorMsg, HttpStatus.NOT_FOUND);
  }
}
