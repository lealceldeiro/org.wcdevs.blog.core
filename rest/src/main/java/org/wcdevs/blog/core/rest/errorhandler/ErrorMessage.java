package org.wcdevs.blog.core.rest.errorhandler;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * Hold information about errors occurred during a request.
 */
public class ErrorMessage {
  // allow message converters to read values
  /**
   * Error message.
   */
  public final String message;
  /**
   * App context.
   */
  public final String context;
  /**
   * Error date time.
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  public final LocalDateTime dateTime;

  /**
   * Creates a new error message with the required info.
   *
   * @param message  Error message.
   * @param context  App error context.
   * @param dateTime Date time the error happened.
   */
  public ErrorMessage(String message, String context, LocalDateTime dateTime) {
    this.message = message;
    this.context = context;
    this.dateTime = dateTime;
  }
}
