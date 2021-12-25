package org.wcdevs.blog.core.rest.errorhandler.impl;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.wcdevs.blog.core.common.exception.NotFoundException;
import org.wcdevs.blog.core.rest.errorhandler.AbstractErrorHandler;
import org.wcdevs.blog.core.rest.errorhandler.ErrorMessage;

/**
 * 404 error handler.
 */
@Component
public class NotFoundErrorHandler extends AbstractErrorHandler {
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
