package org.wcdevs.blog.core.rest.errorhandler;

import java.util.List;
import org.springframework.stereotype.Component;
import org.wcdevs.blog.core.rest.errorhandler.impl.NoopErrorHandler;

/**
 * Creates the handler appropriately to handle REST exceptions.
 */
@Component
public class ErrorHandlerFactory {
  private final ErrorHandler errorHandler;

  ErrorHandlerFactory(List<ErrorHandler> errorHandlers) {
    if (!errorHandlers.isEmpty()) {
      var i = 0;
      while (i < errorHandlers.size() - 1) {
        errorHandlers.get(i).setDelegateHandler(errorHandlers.get(i++));
      }
      errorHandlers.get(i).setDelegateHandler(new NoopErrorHandler());
      errorHandler = errorHandlers.get(0);
    } else {
      errorHandler = new NoopErrorHandler();
    }
  }

  /**
   * Gets the first handler of the chain.
   *
   * @return Returns the handler, chained to other handlers if exist.
   */
  public ErrorHandler getHandler() {
    return errorHandler;
  }
}
