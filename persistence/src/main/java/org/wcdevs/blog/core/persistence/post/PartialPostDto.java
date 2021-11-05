package org.wcdevs.blog.core.persistence.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Data transfer object which contains optional post information.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PartialPostDto.PartialPostDtoBuilder.class)
public class PartialPostDto {
  /**
   * {@link PartialPostDto} builder.
   */
  @JsonPOJOBuilder(withPrefix = "")
  public static class PartialPostDtoBuilder {
  }

  private String title;
  private String slug;
  private String body;
  private LocalDateTime publishedOn;
  private LocalDateTime updatedOn;
}
