package org.wcdevs.blog.core.rest.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

/**
 * REST error handler. It reports errors to the API callers when appropriate.
 */
public interface AppExceptionHandler {
  /**
   * Handles the specified {@code throwable} or, if its implementation can not do it, delegates it
   * to {@code delegatedHandler}.
   *
   * @param throwable {@link Throwable} to be handled.
   * @param request   Web request.
   *
   * @return An error message with the information about the error.
   */
  ResponseEntity<ErrorMessage> handle(Throwable throwable, WebRequest request);

  void setDelegateHandler(AppExceptionHandler delegateHandler);
}
