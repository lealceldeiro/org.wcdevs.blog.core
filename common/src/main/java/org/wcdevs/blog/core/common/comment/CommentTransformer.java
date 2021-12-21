package org.wcdevs.blog.core.common.comment;

import org.springframework.stereotype.Component;
import org.wcdevs.blog.core.common.Transformer;
import org.wcdevs.blog.core.common.util.StringUtils;
import org.wcdevs.blog.core.persistence.comment.Comment;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;
import org.wcdevs.blog.core.persistence.util.ClockUtil;

/**
 * Transformer for classes Comment and CommentDto.
 */
@Component
final class CommentTransformer implements Transformer<Comment, CommentDto, PartialCommentDto> {
  @Override
  public Comment newEntityFromDto(CommentDto dto) {
    return new Comment(StringUtils.slugFrom(dto.getBody()), dto.getBody(), ClockUtil.utcNow(),
                       dto.getPublishedBy(), dto.getPost(), dto.getParentComment());
  }

  @Override
  public CommentDto dtoFromEntity(Comment entity) {
    var parentComment = entity.getParentComment();
    return CommentDto.builder()
                     .postSlug(entity.getPost().getSlug())
                     .parentCommentAnchor(parentComment != null ? parentComment.getAnchor() : null)
                     .body(entity.getBody())
                     .publishedBy(entity.getPublishedBy())
                     .anchor(entity.getAnchor())
                     .lastUpdated(entity.getLastUpdated())
                     .build();
  }

  @Override
  public void update(Comment entity, CommentDto dto) {
    entity.setBody(dto.getBody());
    entity.setLastUpdated(ClockUtil.utcNow());
  }

  @Override
  public void updateNonNullValues(Comment entity, PartialCommentDto dto) {
    if (dto.getBody() != null) {
      entity.setBody(dto.getBody());
    }
    entity.setLastUpdated(ClockUtil.utcNow());
  }
}
