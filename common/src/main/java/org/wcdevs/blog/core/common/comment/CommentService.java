package org.wcdevs.blog.core.common.comment;

import java.util.Collection;
import org.wcdevs.blog.core.persistence.comment.Comment;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;

/**
 * Provides services to handle the business logic concerning the {@link Comment}s data.
 */
public interface CommentService {
  CommentDto createComment(CommentDto commentDto);

  CommentDto getComment(String commentAnchor);

  CommentDto updateComment(String commentAnchor, PartialCommentDto updateCommentDto);

  void deleteComment(String commentAnchor);

  Collection<CommentDto> getAllPostComments(String postSlug);

  Collection<CommentDto> getRootPostComments(String postSlug);

  Collection<CommentDto> getCommentChildComments(String commentAnchor);
}
