package org.wcdevs.blog.core.rest.exceptionhandler.impl;

import java.time.LocalDateTime;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.wcdevs.blog.core.rest.exceptionhandler.AbstractExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.ErrorMessage;

/**
 * Handles exceptions coming from bad API usages.
 */
@Component
public class InvalidArgumentExceptionHandler extends AbstractExceptionHandler {
  @Override
  protected boolean canHandle(Throwable throwable) {
    return throwable instanceof InvalidDataAccessApiUsageException
           || throwable instanceof MethodArgumentTypeMismatchException;
  }

  @Override
  protected ResponseEntity<ErrorMessage> errorFrom(Throwable throwable, WebRequest request) {
    var errorMsg = throwable.getMessage().split(";")[0];
    var errorMessage = new ErrorMessage(errorMsg, request.getContextPath(), LocalDateTime.now());

    return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
  }
}
