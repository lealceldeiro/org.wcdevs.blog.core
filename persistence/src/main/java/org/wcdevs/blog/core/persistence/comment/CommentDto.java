package org.wcdevs.blog.core.persistence.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Data transfer object which contains required comment information. This should be generally used
 * for new comment creation data transfer.
 */
@Getter
@Builder
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CommentDto.CommentDtoBuilder.class)
public class CommentDto {
  /**
   * {@link CommentDto} builder.
   */
  @JsonPOJOBuilder(withPrefix = "")
  public static class CommentDtoBuilder {
  }

  @ToString.Include
  @EqualsAndHashCode.Include
  private String postSlug;

  @ToString.Include
  @EqualsAndHashCode.Include
  private String parentCommentAnchor;

  @NotNull
  @Size(min = 3, max = 2500)
  private String body;

  @NotBlank
  @Size(max = 30)
  private String publishedBy;

  // only to be sent to clients
  @ToString.Include
  @EqualsAndHashCode.Include
  private String anchor;
}
