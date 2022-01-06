package org.wcdevs.blog.core.common;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.PostDto;

public final class TestsUtil {

  private static final Random RANDOM = new Random();

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
                             LocalDateTime.now(), aString()).build();
  }

  public static PostDto buildDto(String title, String slug) {
    return buildDto(title, slug, aString(), aString(), LocalDateTime.now().minusDays(1),
                    LocalDateTime.now(), aString(), aString());
  }

  public static PostDto buildDto(String title, String slug, String body, String excerpt,
                                 LocalDateTime publishedOn, LocalDateTime updatedOn,
                                 String publishedBy, String updatedBy) {
    return dtoBuilder(title, slug, body, excerpt, publishedOn, updatedOn, publishedBy,
                      updatedBy).build();
  }

  public static PostDto.PostDtoBuilder dtoBuilder() {
    return dtoBuilder(aString(), aString(), aString(), aString(), LocalDateTime.now(),
                      LocalDateTime.now(), aString(), aString());
  }

  public static PostDto.PostDtoBuilder dtoBuilder(String title, String slug, String body,
                                                  String excerpt, LocalDateTime publishedOn,
                                                  LocalDateTime updatedOn, String publishedBy,
                                                  String updatedBy) {
    return PostDto.builder()
                  .title(title)
                  .slug(slug)
                  .body(body)
                  .excerpt(excerpt)
                  .publishedOn(publishedOn)
                  .updatedOn(updatedOn)
                  .publishedBy(publishedBy)
                  .updatedBy(updatedBy);
  }

  public static PartialPostDto.PartialPostDtoBuilder partialDtoBuilder(String title, String slug,
                                                                       String body, String excerpt,
                                                                       LocalDateTime publishedOn,
                                                                       LocalDateTime updatedOn,
                                                                       String updatedBy) {
    return PartialPostDto.builder()
                         .title(title)
                         .slug(slug)
                         .body(body)
                         .excerpt(excerpt)
                         .updatedBy(updatedBy);
  }

  public static LocalDateTime randomLocalDateTime() {
    return LocalDateTime.now()
                        .minusDays(RANDOM.nextInt(31))
                        .withHour(RANDOM.nextInt(23))
                        .withMinute(RANDOM.nextInt(59))
                        .withSecond(RANDOM.nextInt(59));
  }

  public static <T> Page<T> pageOf(List<T> content, Pageable pageable) {
    return new PageImpl<>(content, pageable, content.size());
  }

  public static <T> Page<T> pageOf(List<T> content) {
    return pageOf(content, Pageable.ofSize(10));
  }

  @SafeVarargs
  public static <T> Page<T> pageOf(T element, T... elements) {
    return pageOf(Stream.concat(Arrays.stream(elements),
                                Stream.of(element))
                        .collect(Collectors.toList()));
  }

  public static Pageable pageable() {
    return Pageable.ofSize(10);
  }
}
