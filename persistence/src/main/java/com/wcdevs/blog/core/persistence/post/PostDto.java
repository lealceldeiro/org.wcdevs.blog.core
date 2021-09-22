package com.wcdevs.blog.core.persistence.post;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Data transfer object which contains required post information.
 */
public class PostDto {
  @NotNull
  @NotBlank
  @Size(max = 200, min = 3)
  private String title;

  // only to be sent to clients
  private String slug;

  @NotNull
  @NotBlank
  @Size(min = 3)
  private String body;

  // only to be sent to clients
  private LocalDateTime publishedOn;

  public PostDto() {
  }

  /**
   * Creates a new {@link PostDto}.
   *
   * @param title       Post title.
   * @param slug        Post slug (unique relative URL).
   * @param body        Post body (main content).
   * @param publishedOn When the post was published.
   */
  public PostDto(String title, String slug, String body, LocalDateTime publishedOn) {
    this.title = title;
    this.slug = slug;
    this.body = body;
    this.publishedOn = publishedOn;
  }

  // region getters and setters
  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
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

  public void setPublishedOn(LocalDateTime publishedOn) {
    this.publishedOn = publishedOn;
  }
  // endregion
}
