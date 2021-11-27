package org.wcdevs.blog.core.persistence.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import javax.validation.constraints.Size;

/**
 * Data transfer object which contains optional post information.
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PartialPostDto.PartialPostDtoBuilder.class)
public class PartialPostDto {
  /**
   * {@link PartialPostDto} builder.
   */
  @JsonPOJOBuilder(withPrefix = "")
  public static class PartialPostDtoBuilder {
  }

  @EqualsAndHashCode.Include
  @Size(min = 3, max = 200)
  private String title;
  @Size(min = 3, max = 150)
  @EqualsAndHashCode.Include
  private String slug;
  @ToString.Exclude
  @Size(min = 3)
  private String body;
  private LocalDateTime publishedOn;
  private LocalDateTime updatedOn;
}
