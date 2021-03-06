package org.wcdevs.blog.core.rest.exceptionhandler;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

/**
 * An abstract handler defining the core behavior.
 */
@Slf4j
public abstract class AbstractExceptionHandler implements AppExceptionHandler {
  private AppExceptionHandler delegateHandler;

  @Override
  public void setDelegateHandler(AppExceptionHandler delegateHandler) {
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
