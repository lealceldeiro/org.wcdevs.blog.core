package org.wcdevs.blog.core.common.comment;

import java.util.List;
import org.wcdevs.blog.core.persistence.comment.Comment;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;

/**
 * Provides services to handle the business logic concerning the {@link Comment}s data.
 */
public interface CommentService {
  CommentDto createComment(CommentDto postDto);

  CommentDto getComment(String commentAnchor);

  CommentDto updateComment(PartialCommentDto updateCommentDto);

  void deleteComment(String commentAnchor);

  List<CommentDto> getAllPostComments(String postSlug);

  List<CommentDto> getRootPostComments(String postSlug);

  List<CommentDto> getCommentChildComments(String commentAnchor);
}
