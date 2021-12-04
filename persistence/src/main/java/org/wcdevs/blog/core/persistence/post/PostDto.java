package org.wcdevs.blog.core.persistence.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Data transfer object which contains required post information.
 */
@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
  @ToString.Include
  @EqualsAndHashCode.Include
  @Size(min = 3, max = 200)
  private String title;

  @ToString.Include
  @EqualsAndHashCode.Include
  @Size(min = 3, max = 150)
  @Pattern(regexp = "[-a-z0-9]{0,150}")
  private String slug;

  @NotNull
  @NotBlank
  @Size(min = 3)
  private String body;

  @Size(min = 3, max = 250)
  private String excerpt;

  // only to be sent to clients
  private LocalDateTime publishedOn;
  private LocalDateTime updatedOn;

  public PostDto(String title, String slug) {
    this.title = title;
    this.slug = slug;
  }
}
