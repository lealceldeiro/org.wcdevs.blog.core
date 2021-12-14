package org.wcdevs.blog.core.persistence.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/**
 * Data transfer object which contains optional comment information. This should be generally used
 * for data transfer for updating existing comments.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PartialCommentDto.PartialCommentDtoBuilder.class)
public class PartialCommentDto {
  /**
   * {@link PartialCommentDto} builder.
   */
  @JsonPOJOBuilder(withPrefix = "")
  public static class PartialCommentDtoBuilder {
  }

  @NotNull
  @Size(min = 3, max = 2500)
  private String body;
}
