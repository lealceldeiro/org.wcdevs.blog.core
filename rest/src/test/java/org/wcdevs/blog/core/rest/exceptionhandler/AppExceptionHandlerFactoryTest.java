package org.wcdevs.blog.core.rest.exceptionhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AppExceptionHandlerFactoryTest {
  @Test
  void throwsIllegalStateExceptionIfNoHandlersFound() {
    List<AppExceptionHandler> handlers = Collections.emptyList();
    assertThrows(IllegalStateException.class, () -> new ExceptionHandlerFactory(handlers));
  }

  @Test
  void getChainedHandler() {
    List<AppExceptionHandler> handlers = List.of(Mockito.mock(AppExceptionHandler.class));
    var errorHandlerFactory = new ExceptionHandlerFactory(handlers);

    assertEquals(handlers.get(0), errorHandlerFactory.getChainedHandler());
  }
}
