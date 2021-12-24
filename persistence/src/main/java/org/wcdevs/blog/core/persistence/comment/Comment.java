package org.wcdevs.blog.core.persistence.comment;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.wcdevs.blog.core.persistence.post.Post;

/**
 * Entity class that represents a DB table abstraction containing a comment information.
 */
@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Comment {
  @Id
  @GeneratedValue
  @Column(name = "uuid", unique = true, nullable = false)
  @ToString.Include
  private UUID uuid;

  @Column(name = "anchor", unique = true, nullable = false, length = 250)
  @ToString.Include
  private String anchor;

  @Column(name = "body", nullable = false, length = 3000)
  private String body;

  @Column(name = "last_updated", nullable = false)
  private LocalDateTime lastUpdated;

  @Column(name = "published_by", nullable = false, length = 100)
  @ToString.Include
  private String publishedBy;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "post_uuid")
  private Post post;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "parent_comment_uuid")
  private Comment parentComment;

  public Comment(String anchor, String body, LocalDateTime lastUpdated, String publishedBy,
                 Post post) {
    this(anchor, body, lastUpdated, publishedBy, post, null);
  }

  /**
   * Creates a new {@link Comment}.
   *
   * @param anchor        Comment anchor.
   * @param body          Comment body. Main text.
   * @param lastUpdated   Comment when was the comment last updated.
   * @param publishedBy   Author of the comment.
   * @param post          Post which the comment is associated to.
   * @param parentComment Parent comment (if any).
   */
  public Comment(String anchor, String body, LocalDateTime lastUpdated, String publishedBy,
                 Post post, Comment parentComment) {
    this.anchor = anchor;
    this.body = body;
    this.lastUpdated = lastUpdated;
    this.publishedBy = publishedBy;
    this.post = post;
    this.parentComment = parentComment;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
      return false;
    }
    Comment comment = (Comment) other;
    return uuid != null && Objects.equals(uuid, comment.uuid);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
