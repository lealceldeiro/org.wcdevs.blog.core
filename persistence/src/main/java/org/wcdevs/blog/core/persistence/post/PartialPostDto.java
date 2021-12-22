package org.wcdevs.blog.core.persistence.post;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Data transfer object which contains optional post information. This should be generally used
 * for data transfer for updating existing posts.
 */
@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonDeserialize(builder = PartialPostDto.PartialPostDtoBuilder.class)
public class PartialPostDto {
  /**
   * {@link PartialPostDto} builder.
   */
  @JsonPOJOBuilder(withPrefix = "")
  public static class PartialPostDtoBuilder {
  }

  @ToString.Include
  @EqualsAndHashCode.Include
  @Size(min = 3, max = 200)
  private String title;

  @ToString.Include
  @EqualsAndHashCode.Include
  @Pattern(regexp = "[-a-z0-9]{3,150}")
  private String slug;

  @Size(min = 3)
  private String body;

  @Size(min = 3, max = 250)
  private String excerpt;

  @NotBlank
  @Size(max = 30)
  private String updatedBy;
}
