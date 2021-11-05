package org.wcdevs.blog.core.persistence.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Data transfer object which contains required post information.
 */
@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PostDto.PostDtoBuilder.class)
public class PostDto {
  /**
   * {@link PostDto} builder.
   */
  @JsonPOJOBuilder(withPrefix = "")
  public static class PostDtoBuilder {
  }

  @NotNull
  @NotBlank
  @Size(max = 200, min = 3)
  private String title;

  @NotNull
  @NotBlank
  @Size(min = 3)
  private String body;

  // only to be sent to clients
  private String slug;
  private LocalDateTime publishedOn;
  private LocalDateTime updatedOn;

  public PostDto(String title, String slug) {
    this.title = title;
    this.slug = slug;
  }
}
