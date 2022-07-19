package org.wcdevs.blog.core.common.exception;

import java.io.Serial;

/**
 * Exception thrown when an entity is not found.
 */
public class NotFoundException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 6496139832556491573L;

  public NotFoundException(String entityName) {
    super(entityName + " not found");
  }
}
