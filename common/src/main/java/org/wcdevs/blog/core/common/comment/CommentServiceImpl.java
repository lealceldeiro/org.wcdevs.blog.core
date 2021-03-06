package org.wcdevs.blog.core.common.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wcdevs.blog.core.common.post.PostNotFoundException;
import org.wcdevs.blog.core.persistence.comment.Comment;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.CommentRepository;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;
import org.wcdevs.blog.core.persistence.post.PostRepository;

/**
 * Default {@link CommentService} implementation.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final CommentTransformer commentTransformer;

  @Override
  public CommentDto createComment(String postSlug, CommentDto dto) {
    // use lighter query, to later get entity reference
    var post = postRepository.findPostUuidWithSlug(postSlug)
                             .map(postRepository::getReferenceById)
                             .orElseThrow(PostNotFoundException::new);
    var parentComment = commentRepository.getCommentUuidWithAnchor(dto.getParentCommentAnchor())
                                         .map(commentRepository::getReferenceById)
                                         .orElse(null);
    dto.setPost(post);
    dto.setParentComment(parentComment);

    Comment comment = commentRepository.save(commentTransformer.newEntityFromDto(dto));
    return CommentDto.builder().anchor(comment.getAnchor()).build();
  }

  @Override
  @Transactional(readOnly = true)
  public CommentDto getComment(String commentAnchor) {
    var dto = commentRepository.findCommentWithAnchor(commentAnchor);
    if (dto != null) {
      return dto;
    }
    throw new CommentNotFoundException();
  }

  @Override
  public CommentDto updateComment(String commentAnchor, PartialCommentDto dto, String user) {
    Comment comment = commentRepository.findByAnchorAndPublishedBy(commentAnchor, user)
                                       .orElseThrow(CommentNotFoundException::new);
    commentTransformer.updateNonNullValues(comment, dto);
    commentRepository.save(comment);

    return CommentDto.builder().anchor(comment.getAnchor()).build();
  }

  @Override
  public void deleteComment(String commentAnchor) {
    if (commentRepository.deleteByAnchor(commentAnchor) < 1) {
      throw new CommentNotFoundException();
    }
  }

  @Override
  public void deleteComment(String commentAnchor, String user) {
    if (commentRepository.deleteByAnchorAndPublishedBy(commentAnchor, user) < 1) {
      throw new CommentNotFoundException();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommentDto> getAllPostComments(String postSlug, Pageable pageable) {
    return commentRepository.findAllCommentsWithPostSlug(postSlug, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommentDto> getRootPostComments(String postSlug, Pageable pageable) {
    return commentRepository.findRootCommentsWithPostSlug(postSlug, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommentDto> getParentCommentChildren(String parentCommentAnchor, Pageable pageable) {
    return commentRepository.findChildCommentsWithParentAnchor(parentCommentAnchor, pageable);
  }
}
