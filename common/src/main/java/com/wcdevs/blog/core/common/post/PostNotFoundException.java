package com.wcdevs.blog.core.common.post;

import com.wcdevs.blog.core.common.exception.NotFoundException;

/**
 * Exception thrown when an entity is not Found.
 */
public class PostNotFoundException extends NotFoundException {
  private static final long serialVersionUID = 5864134251188658316L;

  public PostNotFoundException() {
    super("Post");
  }
}
