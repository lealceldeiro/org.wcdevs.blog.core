package com.wcdevs.blog.core.persistence.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Data transfer object which contains optional post information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartialPostDto {
  private String title;
  private String slug;
  private String body;
  private LocalDateTime publishedOn;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
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

  public void setBody(String body) {
    this.body = body;
  }

  public LocalDateTime getPublishedOn() {
    return publishedOn;
  }

  public void setPublishedOn(LocalDateTime publishedOn) {
    this.publishedOn = publishedOn;
  }
}
