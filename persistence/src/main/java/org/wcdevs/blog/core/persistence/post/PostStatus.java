package org.wcdevs.blog.core.persistence.post;

import java.util.Arrays;

/**
 * Defines the status for a given post.
 */
public enum PostStatus {
  /**
   * The post is in edition, has some content, but it's not ready for the public yet.
   */
  DRAFT(0),
  /**
   * The post is ready to be published, but it needs to be reviewed before being actually publicly
   * available. Most probably an editor needs to approve its content.
   */
  PENDING(1),
  /**
   * The post is out of edition, it's ready for the public, but it should not be available until a
   * future date.
   */
  SCHEDULED(2),
  /**
   * The post is ready to be viewed, but only to those with a given password.
   */
  PROTECTED(3),
  /**
   * The post is publicly available for everyone.
   */
  PUBLISHED(4),
  /**
   * The post has been marked to be deleted, and it should not be publicly visible.
   */
  TRASHED(5);

  private final int value;

  PostStatus(int value) {
    this.value = value;
  }

  public short shortValue() {
    return (short) value;
  }

  /**
   * Returns an enum {@link PostStatus} equivalent from the provided {@code rawShort} value.
   *
   * @param rawShort {@code short} value to retrieve the {@link PostStatus} equivalent.
   *
   * @return The {@link PostStatus} value which has as a {@link PostStatus#value} the integer
   *         equivalent to the {@code short} version of {@code rawShort}, or {@code null} if none is
   *         found.
   */
  public static PostStatus fromShortValue(short rawShort) {
    return Arrays.stream(PostStatus.values())
                 .filter(status -> status.shortValue() == rawShort)
                 .findAny()
                 .orElse(null);
  }
}
