package org.wcdevs.blog.core.persistence.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.wcdevs.blog.core.persistence.TestsUtil.aRandomPostStatus;
import static org.wcdevs.blog.core.persistence.TestsUtil.aString;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PostDtoTest {
  @Test
  void constructor() {
    var random = new SecureRandom();
    var title = aString();
    var slug = aString();
    var excerpt = aString();
    var status = PostStatus.PUBLISHED.shortValue();
    var commentsCount = Math.abs(random.nextInt());
    var publishedBy = aString();
    var updatedBy = aString();
    var publishedOn = LocalDateTime.now().minusDays(1);
    var updatedOn = LocalDateTime.now();
    var dto = new PostDto(title, slug, excerpt, status, publishedBy, updatedBy, publishedOn,
                          updatedOn, commentsCount);

    assertEquals(title, dto.getTitle());
    assertEquals(slug, dto.getSlug());
    assertEquals(excerpt, dto.getExcerpt());
    assertEquals(publishedBy, dto.getPublishedBy());
    assertEquals(updatedBy, dto.getUpdatedBy());
    assertEquals(publishedOn, dto.getPublishedOn());
    assertEquals(updatedOn, dto.getUpdatedOn());
    assertEquals(commentsCount, dto.getCommentsCount());
  }

  @Test
  void builder() {
    var title = aString();
    var slug = aString();
    var body = aString();
    var excerpt = aString();
    var publishedOn = LocalDateTime.now().minusDays(1);
    var updatedOn = LocalDateTime.now();
    var publishedBy = aString();
    var updatedBy = aString();

    var dto = buildDto(title, slug, body, excerpt, publishedOn, updatedOn, publishedBy, updatedBy);

    assertNotNull(dto);
    assertEquals(title, dto.getTitle());
    assertEquals(slug, dto.getSlug());
    assertEquals(body, dto.getBody());
    assertEquals(excerpt, dto.getExcerpt());
    assertEquals(publishedOn, dto.getPublishedOn());
    assertEquals(updatedOn, dto.getUpdatedOn());
    assertEquals(publishedBy, dto.getPublishedBy());
    assertEquals(updatedBy, dto.getUpdatedBy());
  }

  static Stream<Arguments> dtoWithTitleAndSlugAreEqualsArgs() {
    var title1 = aString();
    var title2 = aString();
    var slug1 = aString();
    var slug2 = aString();
    return Stream.of(arguments(title1, title1, slug1, slug1, true),
                     arguments(title1, title2, slug1, slug1, false),
                     arguments(title1, title1, slug1, slug2, false),
                     arguments(title1, title2, slug1, slug2, false));
  }

  @ParameterizedTest
  @MethodSource("dtoWithTitleAndSlugAreEqualsArgs")
  void dtoWithTitleAndSlugAreEquals(String title1, String title2, String slug1, String slug2,
                                    boolean areEquals) {
    var dto1 = buildDto(title1, slug1);
    var dto2 = buildDto(title2, slug2);
    assertNotNull(dto1);
    assertNotNull(dto2);
    assertEquals(areEquals, dto1.equals(dto2));
    assertEquals(areEquals, dto1.hashCode() == dto2.hashCode());
  }

  private PostDto buildDto() {
    return buildDto(aString(), aString());
  }

  private PostDto buildDto(String title, String slug) {
    return buildDto(title, slug, aString(), aString(), LocalDateTime.now().minusDays(1),
                    LocalDateTime.now(), aString(), aString());
  }

  private PostDto buildDto(String title, String slug, String body, String excerpt,
                           LocalDateTime publishedOn, LocalDateTime updatedOn, String publishedBy,
                           String updatedBy) {
    return PostDto.builder()
                  .title(title)
                  .slug(slug)
                  .body(body)
                  .excerpt(excerpt)
                  .publishedOn(publishedOn)
                  .updatedOn(updatedOn)
                  .publishedBy(publishedBy)
                  .updatedBy(updatedBy)
                  .build();
  }

  @Test
  void toStringContainsFields() {
    var toString = buildDto().toString();
    assertTrue(toString.contains("title="));
    assertTrue(toString.contains("slug="));
  }


  @Test
  void dtoIsEqualToItself() {
    var dto = PostDto.builder().build();
    assertEquals(dto, dto);
  }

  private static Stream<Arguments> dtoIsNotEqualToAnotherWithDifferentSlugOrTiTleArgs() {
    var titleA = aString() + "_titleA";
    var titleB = aString() + "_titleB";
    var slugA = aString() + "_slugA";
    var slugB = aString() + "_slugB";
    return Stream.of(arguments(titleA, titleA, slugA, slugA, true),   // same title/slug: equal DTOs
                     arguments(titleA, titleA, slugA, slugB, false),  // different slugs
                     arguments(titleA, titleB, slugA, slugA, false)); // different titles
  }

  @ParameterizedTest
  @MethodSource("dtoIsNotEqualToAnotherWithDifferentSlugOrTiTleArgs")
  void dtoEqualityDependsOnTitleAndSlug(String title1, String title2, String slug1,
                                        String slug2, boolean shouldTheyBeEqual) {
    var dto1 = PostDto.builder().title(title1).slug(slug1).build();
    var dto2 = PostDto.builder().title(title2).slug(slug2).build();
    assertEquals(shouldTheyBeEqual, dto1.equals(dto2));
  }

  @Test
  void setters() {
    var publishedBy = aString();
    var updatedBy = aString();
    var slug = aString();
    var status = aRandomPostStatus();

    PostDto dto = PostDto.builder().build();
    dto.setPublishedBy(publishedBy);
    dto.setUpdatedBy(updatedBy);
    dto.setSlug(slug);
    dto.setStatus(status);

    assertEquals(publishedBy, dto.getPublishedBy());
    assertEquals(updatedBy, dto.getUpdatedBy());
    assertEquals(slug, dto.getSlug());
    assertEquals(status, dto.getStatus());
  }
}
