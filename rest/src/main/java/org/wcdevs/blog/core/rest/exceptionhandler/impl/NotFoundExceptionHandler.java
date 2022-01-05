package org.wcdevs.blog.core.rest.exceptionhandler.impl;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.wcdevs.blog.core.common.exception.NotFoundException;
import org.wcdevs.blog.core.rest.exceptionhandler.AbstractExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.ErrorMessage;

/**
 * 404 error handler.
 */
@Component
public class NotFoundExceptionHandler extends AbstractExceptionHandler {
  @Override
  protected boolean canHandle(Throwable throwable) {
    return throwable instanceof NotFoundException;
  }

  @Override
  protected ResponseEntity<ErrorMessage> errorFrom(Throwable throwable, WebRequest request) {
    var error = new ErrorMessage(throwable.getMessage(), request.getContextPath(),
                                 LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }
}
