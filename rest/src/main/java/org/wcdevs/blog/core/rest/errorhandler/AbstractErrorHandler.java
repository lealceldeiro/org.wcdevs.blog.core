package org.wcdevs.blog.core.rest.errorhandler;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

/**
 * An abstract handler defining the core behavior.
 */
public abstract class AbstractErrorHandler implements ErrorHandler {
  private ErrorHandler delegateHandler;

  @Override
  public void setDelegateHandler(ErrorHandler delegateHandler) {
    this.delegateHandler = delegateHandler;
  }

  @Override
  public ResponseEntity<ErrorMessage> handle(Throwable throwable, WebRequest request) {
    if (canHandle(throwable)) {
      return errorFrom(throwable, request);
    } else if (delegateHandler != null) {
      return delegateHandler.handle(throwable, request);
    }
    var errorMessage = new ErrorMessage("Internal Server Error", "", LocalDateTime.now());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  protected abstract boolean canHandle(Throwable throwable);

  protected abstract ResponseEntity<ErrorMessage> errorFrom(Throwable throwable, WebRequest req);
}
