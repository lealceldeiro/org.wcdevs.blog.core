package org.wcdevs.blog.core.common.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wcdevs.blog.core.persistence.comment.Comment;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;

/**
 * Provides services to handle the business logic concerning the {@link Comment}s data.
 */
public interface CommentService {
  CommentDto createComment(String postSlug, CommentDto commentDto);

  CommentDto getComment(String commentAnchor);

  CommentDto updateComment(String commentAnchor, PartialCommentDto updateCommentDto, String user);

  void deleteComment(String commentAnchor);

  void deleteComment(String commentAnchor, String user);

  Page<CommentDto> getAllPostComments(String postSlug, Pageable pageable);

  Page<CommentDto> getRootPostComments(String postSlug, Pageable pageable);

  Page<CommentDto> getParentCommentChildren(String parentCommentAnchor, Pageable pageable);
}
