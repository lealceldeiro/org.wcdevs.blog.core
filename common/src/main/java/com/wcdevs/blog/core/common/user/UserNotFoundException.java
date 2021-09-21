package com.wcdevs.blog.core.common.user;

import com.wcdevs.blog.core.common.exception.NotFoundException;

/**
 * Exception thrown when an entity is not Found.
 */
public class UserNotFoundException extends NotFoundException {
  private static final long serialVersionUID = 5864134251188658316L;

  public UserNotFoundException() {
    super("User");
  }
}
