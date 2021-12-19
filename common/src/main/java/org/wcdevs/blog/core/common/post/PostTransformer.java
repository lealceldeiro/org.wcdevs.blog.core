package org.wcdevs.blog.core.common.post;

import java.util.Objects;
import org.springframework.stereotype.Component;
import org.wcdevs.blog.core.common.Transformer;
import org.wcdevs.blog.core.common.util.StringUtils;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;
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

    var slug = Objects.nonNull(dto.getSlug()) // allow user the option to specify a custom slug
               ? dto.getSlug()
               : StringUtils.slugFrom(dto.getTitle());
    var excerpt = excerptFrom(dto.getExcerpt(), dto.getBody());
    var updatedBy = Objects.nonNull(dto.getUpdatedBy()) ? dto.getUpdatedBy() : dto.getPublishedBy();

    // publishedOn and updatedOn will always be determined in the core app
    return new Post(dto.getTitle(), slug, dto.getBody(), excerpt, now, now, dto.getPublishedBy(),
                    updatedBy);
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

  PostDto slugInfo(String slug) {
    return PostDto.builder().slug(slug).build();
  }

  PostDto slugInfo(Post post) {
    return slugInfo(post.getSlug());
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
    post.setUpdatedOn(ClockUtil.utcNow());
  }

  @Override
  public void update(Post post, PostDto newPostDto) {
    post.setTitle(newPostDto.getTitle());
    post.setBody(newPostDto.getBody());
    post.setExcerpt(newPostDto.getExcerpt());
    post.setSlug(newPostDto.getSlug());
    post.setUpdatedBy(newPostDto.getUpdatedBy());

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
                  .build();
  }
}
