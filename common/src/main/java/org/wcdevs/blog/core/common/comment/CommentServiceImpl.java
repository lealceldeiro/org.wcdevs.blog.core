package org.wcdevs.blog.core.common.comment;

import java.util.Collection;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
  public CommentDto createComment(CommentDto dto) {
    // use lighter query, to later get entity reference
    var post = postRepository.findPostUuidWithSlug(dto.getPostSlug())
                             .map(postRepository::getById)
                             .orElseThrow(PostNotFoundException::new);
    var parentComment = commentRepository.getCommentUuidWithAnchor(dto.getParentCommentAnchor())
                                         .map(commentRepository::getById)
                                         .orElse(null);
    dto.setPost(post);
    dto.setParentComment(parentComment);

    Comment comment = commentRepository.save(commentTransformer.newEntityFromDto(dto));
    return CommentDto.builder().anchor(comment.getAnchor()).build();
  }

  @Override
  @Transactional(readOnly = true)
  public CommentDto getComment(String commentAnchor) {
    Comment comment = commentRepository.findByAnchor(commentAnchor)
                                       .orElseThrow(CommentNotFoundException::new);
    var commentDto = commentTransformer.dtoFromEntity(comment);
    commentDto.setChildrenCount(commentChildrenCount(commentAnchor));

    return commentDto;
  }

  private int commentChildrenCount(Comment comment) {
    return commentRepository.countAllByParentComment(comment);
  }

  private int commentChildrenCount(String commentAnchor) {
    return commentRepository.getCommentUuidWithAnchor(commentAnchor)
                            .map(commentRepository::getById)
                            .map(commentRepository::countAllByParentComment)
                            .orElse(0);
  }

  @Override
  public CommentDto updateComment(String commentAnchor, PartialCommentDto updateCommentDto) {
    Comment comment = commentRepository.findByAnchor(commentAnchor)
                                       .orElseThrow(CommentNotFoundException::new);
    commentTransformer.updateNonNullValues(comment, updateCommentDto);
    commentRepository.save(comment);

    var commentDto = commentTransformer.dtoFromEntity(comment);
    commentDto.setChildrenCount(commentChildrenCount(comment));

    return commentDto;
  }

  @Override
  public void deleteComment(String commentAnchor) {
    if (commentRepository.deleteByAnchor(commentAnchor) < 1) {
      throw new CommentNotFoundException();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<CommentDto> getAllPostComments(String postSlug) {
    var comments = commentRepository.findAllWithPostSlug(postSlug);
    setChildrenCountToComments(comments);

    return comments;
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<CommentDto> getRootPostComments(String postSlug) {
    var comments = commentRepository.findAllRootCommentsWithPostSlug(postSlug);
    setChildrenCountToComments(comments);

    return comments;
  }

  private void setChildrenCountToComments(Set<CommentDto> comments) {
    comments.forEach(c -> c.setChildrenCount(commentChildrenCount(c.getAnchor())));
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<CommentDto> getParentCommentChildren(String parentCommentAnchor) {
    var comments = commentRepository.findAllChildCommentsWithParentAnchor(parentCommentAnchor);
    setChildrenCountToComments(comments);

    return comments;
  }
}
