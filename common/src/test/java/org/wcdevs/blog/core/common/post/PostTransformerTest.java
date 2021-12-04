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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.util.ClockUtil;

class PostTransformerTest {
  @Test
  void entityFromPartialDto() {
    try (var mockedClock = mockStatic(ClockUtil.class)) {
      var now = LocalDateTime.now().minusDays(1);
      mockedClock.when(ClockUtil::utcNow).thenReturn(now);
      var dto = dtoBuilder().slug(null).updatedOn(null).publishedOn(null).build();

      var entity = PostTransformer.newEntityFromDto(dto);

      assertNotNull(entity.getSlug());
      assertEquals(now, entity.getPublishedOn());
      assertEquals(now, entity.getUpdatedOn());
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"!@#$%^&*()_+", "1234567890", " @#0hy", "%&dfr ", " #^Y&$($ ", "aText"})
  void entityFromDtoWithComplexPostTitle(String title) {
    var dto = dtoBuilder().slug(null).title(title).build();

    var entity = PostTransformer.newEntityFromDto(dto);

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
    var dto = dtoBuilder().slug(null).title(title).build();

    var entity = PostTransformer.newEntityFromDto(dto);

    var expectedTitleCorrectLengthWithAppendix
        = "Integer in pretium turpis Integer in pretium turpis Integer in pretium turpis Integer "
          + "in pretium turpis Fusce eu sagittis tortor Fusce eu sagittis tortor Fusce eu "
          + "sagittis tortor Fusce eu sagittis ...";
    assertEquals(expectedTitleCorrectLengthWithAppendix, entity.getTitle());
    assertEquals(PostTransformer.TITLE_MAX_LENGTH, entity.getTitle().length());
    assertNotNull(entity.getSlug());
    assertEquals(PostTransformer.SLUG_MAX_LENGTH, entity.getSlug().length());
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
  void entityFromDtoWithLengthySlug(String slug) {
    var dto = dtoBuilder().slug(slug).title("Some title with long slug set already").build();

    var entity = PostTransformer.newEntityFromDto(dto);

    assertNotNull(entity.getSlug());
    assertEquals(PostTransformer.SLUG_MAX_LENGTH, entity.getSlug().length());
  }

  @Test
  void entityFromFullDto() {
    var dto = buildDto();
    var entity = PostTransformer.newEntityFromDto(dto);

    assertEquals(entity.getTitle(), dto.getTitle());
    assertEquals(entity.getSlug(), dto.getSlug());
    assertEquals(entity.getBody(), dto.getBody());
    assertEquals(entity.getExcerpt(), dto.getExcerpt());
    assertNull(entity.getUuid());
  }

  @Test
  void entityFromDtoWillNullBodyNullExcerptThrowsNPE() {
    var now = LocalDateTime.now();
    var dto = buildDto(aString(), aString(), null, null, now, now);

    assertThrows(NullPointerException.class, () -> PostTransformer.newEntityFromDto(dto));
  }

  @Test
  void entityFromDtoWithExactExcerptMatchToMaxLengthWontTrimIt() {
    var excerpt = "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "    // 60 chars
                  + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "  // 60 chars
                  + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "  // 60 chars
                  + "abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde "  // 60 chars
                  + "abcd - abc";                                                   // 10
    var now = LocalDateTime.now();
    var dto = buildDto(aString(), aString(), aString(), excerpt, now, now);

    var post = PostTransformer.newEntityFromDto(dto);

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
    var dto = buildDto(aString(), aString(), aString(), excerpt, now, now);

    var post = PostTransformer.newEntityFromDto(dto);

    assertTrue(post.getExcerpt().endsWith(" word"));
  }

  @Test
  void slugInfoFromSlug() {
    var slug = aString();
    var dto = PostTransformer.slugInfo(slug);
    assertEquals(slug, dto.getSlug());
  }

  @Test
  void slugInfoFromPost() {
    var slug = aString();
    var postMock = mock(Post.class);
    when(postMock.getSlug()).thenReturn(slug);

    var dto = PostTransformer.slugInfo(postMock);

    assertEquals(dto.getSlug(), slug);
  }

  @Test
  void updatePostWithNonNullValuesDoesNotUpdateNullValuesFromDto() {
    var postMock = mock(Post.class);
    var emptyDto = PartialPostDto.builder().build();

    PostTransformer.updatePostWithNonNullValues(postMock, emptyDto);
    verify(postMock, never()).setTitle(any());
    verify(postMock, never()).setBody(any());
  }

  @Test
  void updatePostWithNonNullValuesUpdateValuesNotNullFromDto() {
    var postMock = mock(Post.class);
    var dtoWithValues = buildPartialDto();

    PostTransformer.updatePostWithNonNullValues(postMock, dtoWithValues);
    verify(postMock, times(1)).setTitle(dtoWithValues.getTitle());
    verify(postMock, times(1)).setBody(dtoWithValues.getBody());
    verify(postMock, times(1)).setExcerpt(dtoWithValues.getExcerpt());
  }

  @Test
  void updatePostUpdatesAllValuesFromDto() {
    var postMock = mock(Post.class);
    var dtoWithValues = buildDto();

    PostTransformer.updatePost(postMock, dtoWithValues);
    verify(postMock, times(1)).setTitle(dtoWithValues.getTitle());
    verify(postMock, times(1)).setBody(dtoWithValues.getBody());
  }

  @Test
  void dtoFromEntity() {
    var title = aString();
    var slug = aString();
    var body = aString();
    var excerpt = aString();
    var publishedOn = LocalDateTime.now().minusDays(1);
    var updatedOn = LocalDateTime.now();
    Post entity = new Post(title, slug, body, excerpt, publishedOn, updatedOn);

    var dto = PostTransformer.dtoFromEntity(entity);

    assertEquals(title, dto.getTitle());
    assertEquals(slug, dto.getSlug());
    assertEquals(body, dto.getBody());
    assertEquals(publishedOn, dto.getPublishedOn());
    assertEquals(updatedOn, dto.getUpdatedOn());
  }
}
