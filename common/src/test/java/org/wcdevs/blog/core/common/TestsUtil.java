package org.wcdevs.blog.core.common;

import java.time.LocalDateTime;
import java.util.UUID;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;

public final class TestsUtil {
  private TestsUtil() {
  }

  public static String aString() {
    return UUID.randomUUID().toString();
  }

  public static PostDto buildDto() {
    return buildDto(aString(), aString());
  }

  public static PartialPostDto buildPartialDto() {
    return partialDtoBuilder(aString(), aString(), aString(), aString(), LocalDateTime.now(),
                             LocalDateTime.now()).build();
  }

  public static PostDto buildDto(String title, String slug) {
    return buildDto(title, slug, aString(), aString(), LocalDateTime.now().minusDays(1),
                    LocalDateTime.now());
  }

  public static PostDto buildDto(String title, String slug, String body, String excerpt,
                                 LocalDateTime publishedOn, LocalDateTime updatedOn) {
    return dtoBuilder(title, slug, body, excerpt, publishedOn, updatedOn).build();
  }

  public static PostDto.PostDtoBuilder dtoBuilder() {
    return dtoBuilder(aString(), aString(), aString(), aString(), LocalDateTime.now(),
                      LocalDateTime.now());
  }

  public static PostDto.PostDtoBuilder dtoBuilder(String title, String slug, String body,
                                                  String excerpt, LocalDateTime publishedOn,
                                                  LocalDateTime updatedOn) {
    return PostDto.builder()
                  .title(title)
                  .slug(slug)
                  .body(body)
                  .excerpt(excerpt)
                  .publishedOn(publishedOn)
                  .updatedOn(updatedOn);
  }

  public static PartialPostDto.PartialPostDtoBuilder partialDtoBuilder(String title, String slug,
                                                                       String body, String excerpt,
                                                                       LocalDateTime publishedOn,
                                                                       LocalDateTime updatedOn) {
    return PartialPostDto.builder()
                         .title(title)
                         .slug(slug)
                         .body(body)
                         .excerpt(excerpt)
                         .publishedOn(publishedOn)
                         .updatedOn(updatedOn);
  }

  public static Post entitySample() {
    return new Post(aString(), aString(), aString(), aString(), LocalDateTime.now().minusDays(1),
                    LocalDateTime.now());
  }
}
