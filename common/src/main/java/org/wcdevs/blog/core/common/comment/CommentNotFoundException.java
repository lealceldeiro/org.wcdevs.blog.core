package org.wcdevs.blog.core.common.comment;

import org.wcdevs.blog.core.common.exception.NotFoundException;

/**
 * Exception thrown when a comment is not found.
 */
public class CommentNotFoundException extends NotFoundException {
  private static final long serialVersionUID = -8370715566044587805L;

  public CommentNotFoundException() {
    super("Comment");
  }
}
