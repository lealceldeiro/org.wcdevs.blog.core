package org.wcdevs.blog.core.rest.exceptionhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

class AbstractExceptionHandlerTest {

  @Test
  void handleWillReturn500ByDefaultIfNoAppropriateHandlerIsFound() {
    var defaultHandler = new AbstractExceptionHandler() {
      @Override
      protected boolean canHandle(final Throwable throwable) {
        return false;
      }

      @Override
      protected ResponseEntity<ErrorMessage> errorFrom(Throwable throwable, WebRequest req) {
        return null;
      }
    };

    var actualErrorEntity = defaultHandler.handle(new Throwable(), mock(WebRequest.class));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualErrorEntity.getStatusCode());
    assertNotNull(actualErrorEntity.getBody());
    assertNotNull(actualErrorEntity.getBody().dateTime);
    assertEquals("Internal Server Error", actualErrorEntity.getBody().message);
    assertEquals("", actualErrorEntity.getBody().context);
  }
}
