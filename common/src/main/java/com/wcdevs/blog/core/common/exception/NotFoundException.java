package com.wcdevs.blog.core.common.exception;

/**
 * Exception thrown when an entity is not Found.
 */
public class NotFoundException extends RuntimeException {
  private static final long serialVersionUID = 6496139832556491573L;

  public NotFoundException(String entityName) {
    super(entityName + " not found");
  }
}
