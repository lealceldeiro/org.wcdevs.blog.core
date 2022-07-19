package org.wcdevs.blog.core.common.post;

import org.wcdevs.blog.core.common.exception.NotFoundException;

/**
 * Exception thrown when a post is not found.
 */
public class PostNotFoundException extends NotFoundException {
  @Serial
  private static final long serialVersionUID = 5864134251188658316L;

  public PostNotFoundException() {
    super("Post");
  }
}
