package org.wcdevs.blog.core.persistence.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.wcdevs.blog.core.persistence.post.Post;

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

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime lastUpdated;
  @Setter
  private Integer childrenCount;

  // only to be used among internal components to collaborate on data transfer
  @Getter
  @Setter
  private Post post;
  @Getter
  @Setter
  private Comment parentComment;

  /**
   * Creates a new {@link CommentDto}.
   *
   * @param parentCommentAnchor Comment parent comment anchor ({@code null} if none). If not
   *                            {@code null},it means, this the comment is part of a thread.
   * @param body                Comment content or body.
   * @param publishedBy         User who published the comment.
   * @param anchor              Comment anchor (equivalent to slug in a Post).
   * @param lastUpdated         Last time the comment was updated.
   */
  public CommentDto(String parentCommentAnchor, String body, String publishedBy, String anchor,
                    LocalDateTime lastUpdated) {
    this.parentCommentAnchor = parentCommentAnchor;
    this.body = body;
    this.publishedBy = publishedBy;
    this.anchor = anchor;
    this.lastUpdated = lastUpdated;
  }
}
