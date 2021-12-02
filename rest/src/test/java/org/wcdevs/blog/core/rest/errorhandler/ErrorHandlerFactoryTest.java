package org.wcdevs.blog.core.rest.errorhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ErrorHandlerFactoryTest {
  @Test
  void throwsIllegalStateExceptionIfNoHandlersFound() {
    List<ErrorHandler> handlers = Collections.emptyList();
    assertThrows(IllegalStateException.class, () -> new ErrorHandlerFactory(handlers));
  }

  @Test
  void getChainedHandler() {
    List<ErrorHandler> handlers = List.of(Mockito.mock(ErrorHandler.class));
    var errorHandlerFactory = new ErrorHandlerFactory(handlers);

    assertEquals(handlers.get(0), errorHandlerFactory.getChainedHandler());
  }
}
