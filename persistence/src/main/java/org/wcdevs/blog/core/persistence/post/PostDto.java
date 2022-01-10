package org.wcdevs.blog.core.persistence.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Data transfer object which contains required post information. This should be generally used
 * for new posts creation data transfer.
 */
@Getter
@Builder
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
  @ToString.Include
  @EqualsAndHashCode.Include
  @Size(min = 3, max = 200)
  private String title;

  @ToString.Include
  @EqualsAndHashCode.Include
  @Pattern(regexp = "[-a-z0-9]{3,150}")
  private String slug;

  @NotNull
  @Size(min = 3)
  private String body;

  @Size(min = 3, max = 250)
  private String excerpt;

  @Setter
  @Builder.Default
  private PostStatus status = PostStatus.PUBLISHED;

  // only to be sent to clients
  @Setter
  private String publishedBy;

  @Setter
  private String updatedBy;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime publishedOn;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedOn;

  private Integer commentsCount;

  /**
   * Creates a new {@link PostDto} with only title, slug and excerpt into.
   *
   * @param title            Post title
   * @param slug             Post slug
   * @param excerpt          Post excerpt
   * @param status           Post status
   * @param publishedBy      User who published the post
   * @param updatedBy        User who last updated the post
   * @param publishedOn      {@link LocalDateTime} when the post was published
   * @param updatedOn        {@link LocalDateTime} when the post was last updated
   * @param rawCommentsCount Comments count.
   */
  public PostDto(String title, String slug, String excerpt, short status, String publishedBy,
                 String updatedBy, LocalDateTime publishedOn, LocalDateTime updatedOn,
                 long rawCommentsCount) {
    this.title = title;
    this.slug = slug;
    this.excerpt = excerpt;
    this.status = PostStatus.fromShortValue(status);
    this.publishedBy = publishedBy;
    this.updatedBy = updatedBy;
    this.publishedOn = publishedOn;
    this.updatedOn = updatedOn;
    this.commentsCount = (int) rawCommentsCount;
  }
}
