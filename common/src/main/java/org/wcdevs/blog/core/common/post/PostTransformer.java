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
  static final int TITLE_MAX_LENGTH = 200;
  static final int SLUG_MAX_LENGTH = 150;
  static final int EXCERPT_MAX_LENGTH = 250;

  private PostTransformer() {
    // do not allow instantiation
  }

  static Post newEntityFromDto(PostDto dto) {
    var now = ClockUtil.utcNow();

    // allow user the option to specify a custom slug
    var slug = Objects.nonNull(dto.getSlug()) ? dto.getSlug() : slugFromTitle(dto.getTitle());
    var excerpt = excerptFrom(dto.getExcerpt(), dto.getBody());

    // publishedOn and updatedOn will always be determined in the core app
    return new Post(dto.getTitle(), slug, dto.getBody(), excerpt, now, now);
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
    var hashed = sanitized
                 + (!sanitized.endsWith(SLUG_REPLACEMENT) ? SLUG_REPLACEMENT : "")
                 + Math.abs(Objects.hash(sanitized));

    return sizedSlugFrom(hashed);
  }

  private static String sizedSlugFrom(String candidateSlug) {
    return candidateSlug.length() <= SLUG_MAX_LENGTH
           ? candidateSlug
           : candidateSlug.substring(candidateSlug.length() - SLUG_MAX_LENGTH);
  }

  static void updatePostWithNonNullValues(Post post, PartialPostDto newPostDto) {
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
    post.setUpdatedOn(ClockUtil.utcNow());
  }

  static void updatePost(Post post, PostDto newPostDto) {
    post.setTitle(newPostDto.getTitle());
    post.setBody(newPostDto.getBody());
    post.setExcerpt(newPostDto.getExcerpt());
    post.setSlug(newPostDto.getSlug());

    post.setUpdatedOn(ClockUtil.utcNow());
  }

  static PostDto dtoFromEntity(Post postEntity) {
    return PostDto.builder()
                  .title(postEntity.getTitle())
                  .slug(postEntity.getSlug())
                  .body(postEntity.getBody())
                  .excerpt(postEntity.getExcerpt())
                  .publishedOn(postEntity.getPublishedOn())
                  .updatedOn(postEntity.getUpdatedOn())
                  .build();
  }
}
