package org.wcdevs.blog.core.rest.errorhandler;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.wcdevs.blog.core.rest.errorhandler.impl.DefaultErrorHandler;

/**
 * Creates the handler appropriately to handle REST exceptions.
 */
@Component
public class ErrorHandlerFactory {
  private final ErrorHandler errorHandler;

  ErrorHandlerFactory(Collection<ErrorHandler> availableErrorHandlers) {
    Predicate<ErrorHandler> notDefault = handler -> !(handler instanceof DefaultErrorHandler);
    var customHandlers = availableErrorHandlers.stream()
                                               .filter(notDefault)
                                               .collect(Collectors.toList());
    if (!customHandlers.isEmpty()) {
      for (int i = 0; i < customHandlers.size() - 1; i++) {
        customHandlers.get(i).setDelegateHandler(customHandlers.get(i + 1));
      }
      customHandlers.get(availableErrorHandlers.size() - 2)
                    .setDelegateHandler(new DefaultErrorHandler());
      errorHandler = customHandlers.get(0);
    } else {
      errorHandler = new DefaultErrorHandler();
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
