package org.wcdevs.blog.core.rest.errorhandler;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Creates the handler appropriately to handle REST exceptions.
 */
@Component
public class ErrorHandlerFactory {
  private final ErrorHandler chainedErrorHandler;

  ErrorHandlerFactory(List<ErrorHandler> errorHandlers) {
    if (errorHandlers.isEmpty()) {
      throw new IllegalStateException("No error handlers found.");
    }
    for (int i = 0; i < errorHandlers.size() - 1; i++) {
      errorHandlers.get(i).setDelegateHandler(errorHandlers.get(i + 1));
    }
    chainedErrorHandler = errorHandlers.get(0);
  }

  /**
   * Gets the first handler of the chain.
   *
   * @return Returns the handler, chained to other handlers if exist.
   */
  public ErrorHandler getChainedHandler() {
    return chainedErrorHandler;
  }
}
