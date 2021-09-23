package com.wcdevs.blog.core.rest.util;

import java.time.LocalDateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.context.request.WebRequest;

/**
 * Hold information about errors occurred during a request.
 */
public class ErrorMessage {
  private static final String DATA_INTEGRITY_VIOLATION = "Data integrity violation: ";

  // allow message converters to read values
  public final String message;
  public final String context;
  public final LocalDateTime dateTime;

  private ErrorMessage(String message, String context, LocalDateTime dateTime) {
    this.message = message;
    this.context = context;
    this.dateTime = dateTime;
  }

  /**
   * Creates an error message from a {@link Exception}.
   *
   * @param exception {@link Exception} exception.
   * @param request   Current {@link WebRequest}.
   *
   * @return An {@link ErrorMessage}.
   */
  public static ErrorMessage from(Exception exception, WebRequest request) {
    return new ErrorMessage(exception.getMessage(), request.getContextPath(), LocalDateTime.now());
  }

  /**
   * Creates an error message from a {@link DataIntegrityViolationException}.
   *
   * @param ex      {@link DataIntegrityViolationException} exception.
   * @param request Current {@link WebRequest}.
   *
   * @return An {@link ErrorMessage}.
   */
  public static ErrorMessage from(DataIntegrityViolationException ex,
                                  WebRequest request) {
    // TODO: improve message sent to client
    Throwable cause = ex.getRootCause();
    String message = DATA_INTEGRITY_VIOLATION + (cause != null ? cause.getMessage() : "");

    return new ErrorMessage(message, request.getContextPath(), LocalDateTime.now());
  }
}
