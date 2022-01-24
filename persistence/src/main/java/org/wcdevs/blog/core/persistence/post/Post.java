package org.wcdevs.blog.core.persistence.post;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

/**
 * Entity class that represents a DB table abstraction containing a post information.
 */
@Entity
@Table(name = "post")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Post {
  @Id
  @GeneratedValue
  @Column(name = "uuid", unique = true, nullable = false)
  @ToString.Include
  private UUID uuid;

  @Column(name = "title", nullable = false, length = 250)
  @ToString.Include
  private String title;

  @Column(name = "slug", unique = true, nullable = false, length = 200)
  @ToString.Include
  private String slug;

  @Lob
  @Column(name = "body", nullable = false)
  private String body;

  @Column(name = "excerpt", nullable = false, length = 300)
  private String excerpt;

  @Column(name = "published_on", nullable = false)
  private LocalDateTime publishedOn;

  @Column(name = "updated_on", nullable = false)
  private LocalDateTime updatedOn;

  @Column(name = "published_by", nullable = false, length = 100)
  private String publishedBy;

  @Column(name = "updated_by", nullable = false, length = 100)
  private String updatedBy;

  // see https://www.postgresql.org/docs/current/datatype-numeric.html
  @Column(name = "status", nullable = false, columnDefinition = "smallint")
  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private short status;

  public void setStatus(PostStatus status) {
    this.status = status.shortValue();
  }

  public PostStatus getStatus() {
    return PostStatus.fromShortValue(status);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
      return false;
    }
    Post post = (Post) other;
    return uuid != null && Objects.equals(uuid, post.uuid);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
