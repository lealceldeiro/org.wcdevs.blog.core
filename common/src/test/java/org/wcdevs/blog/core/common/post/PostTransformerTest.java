package org.wcdevs.blog.core.common.post;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
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

      var entity = PostTransformer.entityFromDto(dto);

      assertNotNull(entity.getSlug());
      assertEquals(now, entity.getPublishedOn());
      assertEquals(now, entity.getUpdatedOn());
    }
  }

  @Test
  void entityFromFullDto() {
    var dto = buildDto();
    var entity = PostTransformer.entityFromDto(dto);

    assertEquals(entity.getTitle(), dto.getTitle());
    assertEquals(entity.getSlug(), dto.getSlug());
    assertEquals(entity.getBody(), dto.getBody());
    assertEquals(entity.getPublishedOn(), dto.getPublishedOn());
    assertEquals(entity.getUpdatedOn(), dto.getUpdatedOn());
    assertNull(entity.getUuid());
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
    var publishedOn = LocalDateTime.now().minusDays(1);
    var updatedOn = LocalDateTime.now();
    Post entity = new Post(title, slug, body, publishedOn, updatedOn);

    var dto = PostTransformer.dtoFromEntity(entity);

    assertEquals(title, dto.getTitle());
    assertEquals(slug, dto.getSlug());
    assertEquals(body, dto.getBody());
    assertEquals(publishedOn, dto.getPublishedOn());
    assertEquals(updatedOn, dto.getUpdatedOn());
  }
}
