package org.wcdevs.blog.core.common.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.wcdevs.blog.core.common.TestsUtil.aString;
import static org.wcdevs.blog.core.common.TestsUtil.buildDto;
import static org.wcdevs.blog.core.common.TestsUtil.buildPartialDto;
import static org.wcdevs.blog.core.common.TestsUtil.dtoBuilder;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.wcdevs.blog.core.common.util.StringUtils;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostStatus;
import org.wcdevs.blog.core.persistence.util.ClockUtil;

class PostTransformerTest {
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void newEntityFromDto(boolean isDraft) {
    try (var mockedClock = mockStatic(ClockUtil.class)) {
      var now = LocalDateTime.now().minusDays(1);
      mockedClock.when(ClockUtil::utcNow).thenReturn(now);
      var dto = dtoBuilder().slug(null)
                            .updatedOn(null)
                            .publishedOn(null)
                            .updatedBy(null)
                            .status(isDraft ? PostStatus.DRAFT : PostStatus.PUBLISHED)
                            .build();

      var entity = new PostTransformer().newEntityFromDto(dto);

      assertNotNull(entity.getSlug());
      assertEquals(now, entity.getPublishedOn());
      assertEquals(now, entity.getUpdatedOn());
      assertEquals(dto.getPublishedBy(), entity.getPublishedBy());
      assertEquals(dto.getUpdatedBy(), entity.getUpdatedBy());
    }
  }

  @Test
  void newEntityFromDtoWithUpdatedBy() {
    try (var mockedClock = mockStatic(ClockUtil.class)) {
      var now = LocalDateTime.now().minusDays(1);
      mockedClock.when(ClockUtil::utcNow).thenReturn(now);
      var dto = dtoBuilder().build();

      var entity = new PostTransformer().newEntityFromDto(dto);

      assertEquals(dto.getUpdatedBy(), entity.getUpdatedBy());
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"!@#$%^&*()_+", "1234567890", " @#0hy", "%&dfr ", " #^Y&$($ ", "aText"})
  void entityFromDtoWithComplexPostTitle(String title) {
    var dto = dtoBuilder().slug(null).title(title).build();

    var entity = new PostTransformer().newEntityFromDto(dto);

    assertNotNull(entity.getSlug());
    assertFalse(entity.getSlug().contains("--"));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      // large
      "Integer in pretium turpis Integer in pretium turpis Integer in pretium turpis Integer in "
      + "pretium turpis Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor "
      + "Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu "
      + "sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor",
      "Integer in pretium turpis Integer in pretium turpis Integer in pretium turpis Integer in "
      // extra large
      + "pretium turpis Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor "
      + "Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu "
      + "sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor"
      + "pretium turpis Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor "
      + "Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu "
      + "sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor"
      + "pretium turpis Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor "
      + "Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu "
      + "sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor"
      + "pretium turpis Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor "
      + "Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu "
      + "sagittis tortor Fusce eu sagittis tortor Fusce eu sagittis tortor",
  })
  void entityFromDtoWithLengthyTitle(String title) {
    var dto = dtoBuilder().title(title).slug(null).build();

    var entity = new PostTransformer().newEntityFromDto(dto);

    assertNotNull(entity.getSlug());
    assertEquals(StringUtils.SLUG_MAX_LENGTH, entity.getSlug().length());
  }

  @Test
  void entityFromFullDto() {
    var dto = buildDto();
    var entity = new PostTransformer().newEntityFromDto(dto);

    assertEquals(entity.getTitle(), dto.getTitle());
    assertEquals(entity.getSlug(), dto.getSlug());
    assertEquals(entity.getBody(), dto.getBody());
    assertEquals(entity.getExcerpt(), dto.getExcerpt());
    assertEquals(entity.getStatus(), dto.getStatus());
    assertNull(entity.getUuid());
  }

  @Test
  void entityFromDtoWillNullBodyNullExcerptThrowsNPE() {
    var now = LocalDateTime.now();
    var dto = buildDto(aString(), aString(), null, null, now, now, aString(), aString());

    Executable newEntityFromDto = () -> new PostTransformer().newEntityFromDto(dto);
    assertThrows(NullPointerException.class, newEntityFromDto);
  }

  @Test
  void entityFromDtoWithExactExcerptMatchToMaxLengthWontTrimIt() {
    var excerpt = "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "    // 60 chars
                  + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "  // 60 chars
                  + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "  // 60 chars
                  + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "  // 60 chars
                  + "abcd - abc";                                                   // 10
    var now = LocalDateTime.now();
    var dto = buildDto(aString(), aString(), aString(), excerpt, now, now, aString(), aString());

    var post = new PostTransformer().newEntityFromDto(dto);

    assertEquals(excerpt, post.getExcerpt());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "      // 60 chars
      + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "    // 60 chars
      + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "    // 60 chars
      + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "    // 60 chars
      + "li word abc",  // a simple trim would cut after 'b', before 'c'  // 11 (total 251)
      "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "      // 60 chars
      + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "    // 60 chars
      + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "    // 60 chars
      + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "    // 60 chars
      + "deThe word ter"                                                  // 14 (total 254)
  })
  void entityFromDtoWithExcerptLongerIsTrimmedAtLastSpaceBeforeWordToMaxLength(String excerpt) {
    var now = LocalDateTime.now();
    var dto = buildDto(aString(), aString(), aString(), excerpt, now, now, aString(), aString());

    var post = new PostTransformer().newEntityFromDto(dto);

    assertTrue(post.getExcerpt().endsWith(" word"));
  }

  @Test
  void slugInfoFromPost() {
    var slug = aString();
    var postMock = mock(Post.class);
    when(postMock.getSlug()).thenReturn(slug);
    when(postMock.getStatus()).thenReturn(PostStatus.PENDING);

    var dto = new PostTransformer().slugInfo(postMock);

    assertEquals(slug, dto.getSlug());
    assertEquals(PostStatus.PENDING, dto.getStatus());
  }

  @Test
  void updatePostWithNonNullValuesDoesNotUpdateNullValuesFromDto() {
    var postMock = mock(Post.class);
    var emptyDto = PartialPostDto.builder().build();

    new PostTransformer().updateNonNullValues(postMock, emptyDto);
    verify(postMock, never()).setTitle(any());
    verify(postMock, never()).setBody(any());
    verify(postMock, never()).setExcerpt(any());
    verify(postMock, never()).setPublishedBy(any());
    verify(postMock, never()).setUpdatedBy(any());
    verify(postMock, times(1)).setUpdatedOn(any());
  }

  @Test
  void updatePostWithNonNullValuesUpdateValuesNotNullFromDto() {
    var postMock = mock(Post.class);
    var dtoWithValues = buildPartialDto();

    new PostTransformer().updateNonNullValues(postMock, dtoWithValues);
    verify(postMock, times(1)).setTitle(dtoWithValues.getTitle());
    verify(postMock, times(1)).setBody(dtoWithValues.getBody());
    verify(postMock, times(1)).setExcerpt(dtoWithValues.getExcerpt());
    verify(postMock, never()).setPublishedBy(any());
    verify(postMock, times(1)).setUpdatedBy(any());
    verify(postMock, times(1)).setUpdatedOn(any());
  }

  @Test
  void updatePostUpdatesAllValuesFromDto() {
    var postMock = mock(Post.class);
    var dtoWithValues = buildDto();

    new PostTransformer().update(postMock, dtoWithValues);
    verify(postMock, times(1)).setTitle(dtoWithValues.getTitle());
    verify(postMock, times(1)).setBody(dtoWithValues.getBody());
    verify(postMock, times(1)).setExcerpt(dtoWithValues.getExcerpt());
    verify(postMock, times(1)).setUpdatedBy(dtoWithValues.getUpdatedBy());
    verify(postMock, never()).setPublishedBy(any());
    verify(postMock, times(1)).setUpdatedOn(any());
  }

  @Test
  void dtoFromEntity() {
    var title = aString();
    var slug = aString();
    var body = aString();
    var excerpt = aString();
    var publishedOn = LocalDateTime.now().minusDays(1);
    var updatedOn = LocalDateTime.now();
    var publishedBy = aString();
    var updatedBy = aString();
    var entity = Post.builder()
                     .title(title)
                     .slug(slug)
                     .body(body)
                     .excerpt(excerpt)
                     .publishedOn(publishedOn)
                     .updatedOn(updatedOn)
                     .publishedBy(publishedBy)
                     .updatedBy(updatedBy)
                     .status(PostStatus.PUBLISHED.shortValue())
                     .build();

    var dto = new PostTransformer().dtoFromEntity(entity);

    assertEquals(title, dto.getTitle());
    assertEquals(slug, dto.getSlug());
    assertEquals(body, dto.getBody());
    assertEquals(publishedOn, dto.getPublishedOn());
    assertEquals(updatedOn, dto.getUpdatedOn());
    assertEquals(publishedBy, dto.getPublishedBy());
    assertEquals(updatedBy, dto.getUpdatedBy());
    assertEquals(PostStatus.PUBLISHED, dto.getStatus());
  }
}
