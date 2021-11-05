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
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Post {
  @Id
  @GeneratedValue
  @Column(name = "uuid", unique = true, nullable = false)
  @ToString.Include
  private UUID uuid;

  @Column(name = "title", unique = true, nullable = false, length = 200)
  @ToString.Include
  private String title;

  @Column(name = "slug", unique = true, nullable = false, length = 150)
  @ToString.Include
  private String slug;

  @Lob
  @Column(name = "body", nullable = false)
  private String body;

  @Column(name = "published_on", nullable = false)
  private LocalDateTime publishedOn;

  @Column(name = "updated_on", nullable = false)
  private LocalDateTime updatedOn;

  /**
   * Creates a new {@link Post}.
   *
   * @param title       Post title (must be unique)
   * @param slug        Post slug (must be unique)
   * @param body        Post body
   * @param publishedOn Date time when the post was published.
   * @param updatedOn   Date time when the post was last updated.
   */
  public Post(String title, String slug, String body, LocalDateTime publishedOn,
              LocalDateTime updatedOn) {
    this.title = title;
    this.slug = slug;
    this.body = body;
    this.publishedOn = publishedOn;
    this.updatedOn = updatedOn;
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
