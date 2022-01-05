package org.wcdevs.blog.core.rest.exceptionhandler.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.wcdevs.blog.core.rest.exceptionhandler.AbstractExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.ErrorMessage;

/**
 * 409 because of DB restriction error handler.
 */
@Component
public class DataIntegrityViolationExceptionHandler extends AbstractExceptionHandler {
  private static final Pattern FIELD_NAME_MATCHER = Pattern.compile("(?<=\\().*(?=\\)=)");
  private static final Pattern FIELD_VALUE_MATCHER = Pattern.compile("(?<==\\().*(?=\\))");

  @Override
  protected boolean canHandle(Throwable throwable) {
    return throwable instanceof DataIntegrityViolationException;
  }

  @Override
  protected ResponseEntity<ErrorMessage> errorFrom(Throwable throwable, WebRequest request) {
    var errorMsg = throwable.getMessage();
    var rootCauseMessage = rootCauseMessage(throwable);

    if (rootCauseMessage.contains("ERROR: duplicate key value violates unique constraint")) {
      errorMsg = String.format("There's already a %s with value '%s'",
                               fieldName(rootCauseMessage), fieldValue(rootCauseMessage));
    }
    return new ResponseEntity<>(new ErrorMessage(errorMsg, request.getContextPath(),
                                                 LocalDateTime.now()),
                                HttpStatus.CONFLICT);
  }

  private String rootCauseMessage(Throwable throwable) {
    var rootCause = ((DataIntegrityViolationException) throwable).getRootCause();

    return Objects.nonNull(rootCause) && Objects.nonNull(rootCause.getMessage())
           ? rootCause.getMessage()
           : "";
  }

  private String fieldName(String rootCauseMessage) {
    return match(rootCauseMessage, FIELD_NAME_MATCHER, "<field_not_found>");
  }

  private String fieldValue(String rootCauseMessage) {
    return match(rootCauseMessage, FIELD_VALUE_MATCHER, "<value_not_found>");
  }

  private String match(String rootCauseMessage, Pattern pattern, String defaultValue) {
    var matcher = pattern.matcher(rootCauseMessage);
    if (matcher.find()) {
      return matcher.group();
    }
    return defaultValue;
  }
}
