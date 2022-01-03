package org.wcdevs.blog.core.rest.errorhandler;

import java.time.LocalDateTime;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

/**
 * An abstract handler defining the core behavior.
 */
@Log4j2
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

    log.warn("There has been an error handled in a non-customized way", throwable);
    var errorMessage = new ErrorMessage("Internal Server Error", "", LocalDateTime.now());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  protected abstract boolean canHandle(Throwable throwable);

  protected abstract ResponseEntity<ErrorMessage> errorFrom(Throwable throwable, WebRequest req);
}
