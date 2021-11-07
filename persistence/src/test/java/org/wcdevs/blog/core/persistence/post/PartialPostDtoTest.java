package org.wcdevs.blog.core.persistence.post;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.wcdevs.blog.core.persistence.TestsUtil.aString;

class PartialPostDtoTest {
  @Test
  void builder() {
    var body = aString();
    var slug = aString();
    var publishedOn = LocalDateTime.now().minusDays(1);
    var title = aString();
    var updatedOn = LocalDateTime.now();
    var dto = buildDto(title, slug, body, publishedOn, updatedOn);
    assertNotNull(dto);
    assertEquals(body, dto.getBody());
    assertEquals(title, dto.getTitle());
    assertEquals(slug, dto.getSlug());
    assertEquals(publishedOn, dto.getPublishedOn());
    assertEquals(updatedOn, dto.getUpdatedOn());
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

  private PartialPostDto buildDto() {
    return buildDto(aString(), aString());
  }

  private PartialPostDto buildDto(String title, String slug) {
    return buildDto(title, slug, aString(), LocalDateTime.now().minusDays(1),
                    LocalDateTime.now());
  }

  private PartialPostDto buildDto(String title, String slug, String body, LocalDateTime publishedOn,
                                  LocalDateTime updatedOn) {
    return PartialPostDto.builder()
                         .title(title)
                         .slug(slug)
                         .body(body)
                         .publishedOn(publishedOn)
                         .updatedOn(updatedOn)
                         .build();
  }

  @Test
  void toStringContainsFields() {
    var toString = buildDto().toString();
    assertTrue(toString.contains("title="));
    assertTrue(toString.contains("slug="));
    assertTrue(toString.contains("publishedOn="));
    assertTrue(toString.contains("updatedOn="));
  }
}
