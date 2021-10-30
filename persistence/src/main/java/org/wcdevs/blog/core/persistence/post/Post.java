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

/**
 * Entity class that represents a DB table abstraction containing a post information.
 */
@Entity
@Table(name = "post")
public class Post {
  @Id
  @GeneratedValue
  @Column(name = "uuid", unique = true, nullable = false)
  private UUID uuid;

  @Column(name = "title", unique = true, nullable = false, length = 200)
  private String title;

  @Column(name = "slug", unique = true, nullable = false, length = 150)
  private String slug;

  @Lob
  @Column(name = "body", nullable = false)
  private String body;

  @Column(name = "published_on", nullable = false)
  private LocalDateTime publishedOn;

  public Post() {
  }

  /**
   * Creates a new {@link Post}.
   *
   * @param title       Post title (must be unique)
   * @param slug        Post slug (must be unique)
   * @param body        Post body
   * @param publishedOn Date time when the post was published.
   */
  public Post(String title, String slug, String body, LocalDateTime publishedOn) {
    this.title = title;
    this.slug = slug;
    this.body = body;
    this.publishedOn = publishedOn;
  }

  // region getters and setters
  UUID getUuid() {
    return uuid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getSlug() {
    return slug;
  }

  void setSlug(final String slug) {
    this.slug = slug;
  }

  public String getBody() {
    return body;
  }

  public void setBody(final String body) {
    this.body = body;
  }

  public LocalDateTime getPublishedOn() {
    return publishedOn;
  }

  void setPublishedOn(final LocalDateTime publishedOn) {
    this.publishedOn = publishedOn;
  }
  // endregion

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    Post otherPost = (Post) other;
    return Objects.equals(this.getTitle(), otherPost.getTitle())
           && Objects.equals(this.getSlug(), otherPost.getSlug());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTitle(), getSlug());
  }
}
