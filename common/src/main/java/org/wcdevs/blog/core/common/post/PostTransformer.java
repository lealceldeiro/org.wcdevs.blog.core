package org.wcdevs.blog.core.common.post;

import static org.wcdevs.blog.core.common.util.StringUtils.emptyIfNull;

import java.util.Objects;
import org.springframework.stereotype.Component;
import org.wcdevs.blog.core.common.Transformer;
import org.wcdevs.blog.core.common.util.StringUtils;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.persistence.post.PostStatus;
import org.wcdevs.blog.core.persistence.util.ClockUtil;

/**
 * Transformer for classes Post and PostDto.
 */
@Component
final class PostTransformer implements Transformer<Post, PostDto, PartialPostDto> {
  static final int EXCERPT_MAX_LENGTH = 250;

  @Override
  public Post newEntityFromDto(PostDto dto) {
    var now = ClockUtil.utcNow();
    var isDraft = dto.getStatus() == PostStatus.DRAFT;

    var excerpt = isDraft ? emptyIfNull(dto.getExcerpt())
                          : excerptFrom(dto.getExcerpt(), dto.getBody());

    return Post.builder()
               .title(isDraft ? emptyIfNull(dto.getTitle()) : dto.getTitle())
               .slug(slug(dto))
               .body(isDraft ? emptyIfNull(dto.getBody()) : dto.getBody())
               .excerpt(excerpt)
               .publishedBy(dto.getPublishedBy())
               .publishedOn(now)
               .updatedOn(now)
               .updatedBy(dto.getUpdatedBy())
               .status(dto.getStatus().shortValue())
               .build();
  }

  private static String slug(PostDto dto) {
    var rawSlug = dto.getSlug();
    if (dto.getStatus() == PostStatus.DRAFT) {
      return emptyIfNull(rawSlug);
    }
    // users can specify a custom slug
    return rawSlug != null ? rawSlug : StringUtils.slugFrom(dto.getTitle());
  }

  private static String excerptFrom(String excerptCandidate, String bodyToCreateExcerpt) {
    var candidate = Objects.nonNull(excerptCandidate) ? excerptCandidate : bodyToCreateExcerpt;
    if (candidate.length() <= EXCERPT_MAX_LENGTH) {
      return candidate;
    }
    var stripped = Objects.requireNonNull(candidate).strip();
    var trimmed = stripped.substring(0, EXCERPT_MAX_LENGTH);

    if (!" ".equals(stripped.substring(EXCERPT_MAX_LENGTH, EXCERPT_MAX_LENGTH + 1))) {
      var spaceIndex = trimmed.lastIndexOf(" ");
      return spaceIndex != -1 ? trimmed.substring(0, spaceIndex) : trimmed;
    }

    return trimmed;
  }

  PostDto slugInfo(Post post) {
    return PostDto.builder()
                  .slug(post.getSlug())
                  .status(post.getStatus())
                  .build();
  }

  @Override
  public void updateNonNullValues(Post post, PartialPostDto newPostDto) {
    if (Objects.nonNull(newPostDto.getTitle())) {
      post.setTitle(newPostDto.getTitle());
    }
    if (Objects.nonNull(newPostDto.getSlug())) {
      post.setSlug(newPostDto.getSlug());
    }
    if (Objects.nonNull(newPostDto.getBody())) {
      post.setBody(newPostDto.getBody());
    }
    if (Objects.nonNull(newPostDto.getExcerpt())) {
      post.setExcerpt(newPostDto.getExcerpt());
    }
    if (Objects.nonNull(newPostDto.getUpdatedBy())) {
      post.setUpdatedBy(newPostDto.getUpdatedBy());
    }
    // don't change the status in a partial update: drafts can have missing data

    post.setUpdatedOn(ClockUtil.utcNow());
  }

  @Override
  public void update(Post post, PostDto newPostDto) {
    post.setTitle(newPostDto.getTitle());
    post.setSlug(newPostDto.getSlug());
    post.setBody(newPostDto.getBody());
    post.setExcerpt(newPostDto.getExcerpt());
    post.setUpdatedBy(newPostDto.getUpdatedBy());
    post.setStatus(newPostDto.getStatus());

    post.setUpdatedOn(ClockUtil.utcNow());
  }

  @Override
  public PostDto dtoFromEntity(Post postEntity) {
    return PostDto.builder()
                  .title(postEntity.getTitle())
                  .slug(postEntity.getSlug())
                  .body(postEntity.getBody())
                  .excerpt(postEntity.getExcerpt())
                  .publishedOn(postEntity.getPublishedOn())
                  .updatedOn(postEntity.getUpdatedOn())
                  .publishedBy(postEntity.getPublishedBy())
                  .updatedBy(postEntity.getUpdatedBy())
                  .status(postEntity.getStatus())
                  .build();
  }
}
