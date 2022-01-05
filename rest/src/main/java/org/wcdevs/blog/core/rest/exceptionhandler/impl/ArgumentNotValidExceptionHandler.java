package org.wcdevs.blog.core.rest.exceptionhandler.impl;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.wcdevs.blog.core.rest.exceptionhandler.AbstractExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.ErrorMessage;

/**
 * Handler to intercept validation errors from the request, and reported by the validation
 * framework. It will customize the response to client, removing the sensitive information.
 */
@Component
public class ArgumentNotValidExceptionHandler extends AbstractExceptionHandler {
  private static final Pattern FIELD_NAME_MATCHER = Pattern.compile("(?<=on field ')\\w+(?=')");
  private static final Pattern FIELD_VALUE_MATCHER
      = Pattern.compile("(?<=rejected value \\[)[^]]*(?=]; codes)");
  private static final Pattern INFO_ERROR_MESSAGE_MATCHER_AFTER_LAST_SEMICOLON
      = Pattern.compile("(?<=default message \\[).*(?=]](\\n|\\r|$)?)");

  @Override
  protected boolean canHandle(Throwable throwable) {
    return throwable instanceof MethodArgumentNotValidException;
  }

  @Override
  protected ResponseEntity<ErrorMessage> errorFrom(Throwable throwable, WebRequest request) {
    var errorMsg = throwable.getMessage();

    if (errorMsg.startsWith("Validation failed for argument")) {
      errorMsg = String.format("Incorrect value '%s' for field '%s'. Error: %s",
                               fieldValue(errorMsg), fieldName(errorMsg),
                               frameworkErrorMessage(errorMsg));
    }

    return new ResponseEntity<>(new ErrorMessage(errorMsg, request.getContextPath(),
                                                 LocalDateTime.now()),
                                HttpStatus.BAD_REQUEST);
  }

  private String fieldName(String msg) {
    return match(msg, FIELD_NAME_MATCHER, "<field_not_found>");
  }

  private String fieldValue(String rootCauseMessage) {
    return match(rootCauseMessage, FIELD_VALUE_MATCHER, "<value_not_found>");
  }

  private String frameworkErrorMessage(String errorMessage) {
    return match(errorMessage, INFO_ERROR_MESSAGE_MATCHER_AFTER_LAST_SEMICOLON,
                 "<framework_error_message_not_found>", errorMessage.lastIndexOf(";"));
  }

  private String match(String msg, Pattern pattern, String defaultValue) {
    return match(msg, pattern, defaultValue, 0);
  }

  private String match(String msg, Pattern pattern, String defaultValue, int startMatchingPos) {
    var matcher = pattern.matcher(msg);
    if (matcher.find(startMatchingPos)) {
      return matcher.group();
    }
    return defaultValue;
  }
}
