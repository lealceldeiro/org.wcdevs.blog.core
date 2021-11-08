package org.wcdevs.blog.core.common.post;

import java.util.Locale;
import java.util.Objects;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.persistence.util.ClockUtil;

/**
 * Transformer for classes User and UserDto.
 */
final class PostTransformer {
  private static final String SLUG_REPLACEMENT = "-";
  private static final String SLUG_REPLACE_REGEX = "[^a-z0-9]++";

  private PostTransformer() {
    // do not allow instantiation
  }

  static Post entityFromDto(PostDto dto) {
    var now = ClockUtil.utcNow();

    var slug = dto.getSlug() != null ? dto.getSlug() : slugFromTitle(dto.getTitle());
    var publishedOn = dto.getPublishedOn() != null ? dto.getPublishedOn() : now;
    var updatedOn = dto.getUpdatedOn() != null ? dto.getUpdatedOn() : now;

    return new Post(dto.getTitle(), slug, dto.getBody(), publishedOn, updatedOn);
  }

  static PostDto slugInfo(String slug) {
    return PostDto.builder().slug(slug).build();
  }

  static PostDto slugInfo(Post post) {
    return slugInfo(post.getSlug());
  }

  private static String slugFromTitle(String title) {
    var sanitized = Objects.requireNonNull(title)
                           .toLowerCase(Locale.ENGLISH)
                           .strip()
                           .replaceAll(SLUG_REPLACE_REGEX, SLUG_REPLACEMENT);
    return sanitized
           + (!sanitized.endsWith(SLUG_REPLACEMENT) ? SLUG_REPLACEMENT : "")
           + Objects.hash(sanitized);
  }

  static void updatePostWithNonNullValues(Post post, PartialPostDto newPostDto) {
    if (isNotNull(newPostDto.getTitle())) {
      post.setTitle(newPostDto.getTitle());
    }
    if (isNotNull(newPostDto.getBody())) {
      post.setBody(newPostDto.getBody());
    }
  }

  static void updatePost(Post post, PostDto newPostDto) {
    post.setTitle(newPostDto.getTitle());
    post.setBody(newPostDto.getBody());
  }

  private static boolean isNotNull(Object o) {
    return null != o;
  }

  static PostDto dtoFromEntity(Post postEntity) {
    return PostDto.builder()
                  .title(postEntity.getTitle())
                  .slug(postEntity.getSlug())
                  .body(postEntity.getBody())
                  .publishedOn(postEntity.getPublishedOn())
                  .updatedOn(postEntity.getUpdatedOn())
                  .build();
  }
}
