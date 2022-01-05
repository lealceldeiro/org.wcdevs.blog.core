package org.wcdevs.blog.core.rest.exceptionhandler;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Creates the handler appropriately to handle REST exceptions.
 */
@Component
public class ExceptionHandlerFactory {
  private final AppExceptionHandler chainedAppExceptionHandler;

  ExceptionHandlerFactory(List<AppExceptionHandler> exceptionHandlers) {
    if (exceptionHandlers.isEmpty()) {
      throw new IllegalStateException("No exception handlers found.");
    }
    for (int i = 0; i < exceptionHandlers.size() - 1; i++) {
      exceptionHandlers.get(i).setDelegateHandler(exceptionHandlers.get(i + 1));
    }
    chainedAppExceptionHandler = exceptionHandlers.get(0);
  }

  /**
   * Gets the first handler of the chain.
   *
   * @return Returns the handler, chained to other handlers if exist.
   */
  public AppExceptionHandler getChainedHandler() {
    return chainedAppExceptionHandler;
  }
}
