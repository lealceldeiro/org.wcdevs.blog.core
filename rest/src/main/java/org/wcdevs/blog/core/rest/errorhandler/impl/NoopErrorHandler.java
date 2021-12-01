package org.wcdevs.blog.core.rest.errorhandler.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.wcdevs.blog.core.rest.errorhandler.AbstractErrorHandler;
import org.wcdevs.blog.core.rest.errorhandler.ErrorMessage;

/**
 * Error handler to be use in absence of any better fit.
 */
public class NoopErrorHandler extends AbstractErrorHandler {
  @Override
  protected boolean canHandle(Throwable throwable) {
    return false;
  }

  @Override
  protected ResponseEntity<ErrorMessage> errorFrom(Throwable throwable, WebRequest request) {
    return null;
  }
}
