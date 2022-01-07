package org.wcdevs.blog.core.persistence.post;

import lombok.RequiredArgsConstructor;

/**
 * Defines the status for a given post.
 *
 * @apiNote Maintainers of this class MUST NOT modify the order of the existing enum values. The
 *          current JPA mappings using these values rely on the ordinal values automatically
 *          assigned to each enum. While adding new values, they MUST be placed AFTER the last enum
 *          value at the moment.
 * @see <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html">Enum Types</a>
 * @see Enum#ordinal()
 */
@RequiredArgsConstructor
public enum PostStatus {
  /**
   * The post is in edition, has some content, but it's not ready for the public yet.
   */
  DRAFT,
  /**
   * The post is ready to be published, but it needs to be reviewed before being actually publicly
   * available. Most probably an editor needs to approve its content.
   */
  PENDING,
  /**
   * The post is out of edition, it's ready for the public, but it should not be available until a
   * future date.
   */
  SCHEDULED,
  /**
   * The post is ready to be viewed, but only to those with a given password.
   */
  PROTECTED,
  /**
   * The post is publicly available for everyone.
   */
  PUBLISHED,
  /**
   * The post has been marked to be deleted, and it should not be publicly visible.
   */
  TRASHED
}
