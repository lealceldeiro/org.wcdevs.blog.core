package com.wcdevs.blog.core.common.post;

import com.wcdevs.blog.core.persistence.post.PartialPostDto;
import com.wcdevs.blog.core.persistence.post.Post;
import com.wcdevs.blog.core.persistence.post.PostDto;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;

/**
 * Transformer for classes User and UserDto.
 */
final class PostTransformer {
  private static final ZoneId BASE_ZONE_ID = ZoneId.of("UTC");
  private static final String SLUG_REPLACEMENT = "-";
  private static final String SLUG_REPLACE_REGEX = "[^a-z0-9]++";

  private PostTransformer() {
    // do not allow instantiation
  }

  static Post entityFromDto(PostDto dto) {
    String slug = dto.getSlug() != null ? dto.getSlug() : slugFromTitle(dto.getTitle());
    LocalDateTime publishedOn = dto.getPublishedOn() != null
                                ? dto.getPublishedOn()
                                : LocalDateTime.now(BASE_ZONE_ID);

    return new Post(dto.getTitle(), slug, dto.getBody(), publishedOn);
  }

  static PartialPostDto slugInfo(String slug) {
    PartialPostDto info = new PartialPostDto();
    info.setSlug(slug);
    return info;
  }

  static PartialPostDto slugInfo(Post post) {
    PartialPostDto info = new PartialPostDto();
    if (isNotNull(post)) {
      info.setSlug(post.getSlug());
    }
    return info;
  }

  private static String slugFromTitle(String title) {
    String sanitized = Objects.requireNonNull(title)
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

  private static boolean isNotNull(Object o) {
    return null != o;
  }

  static PostDto dtoFromEntity(Post postEntity) {
    return new PostDto(postEntity.getTitle(), postEntity.getSlug(), postEntity.getBody(),
                       postEntity.getPublishedOn());
  }
}
