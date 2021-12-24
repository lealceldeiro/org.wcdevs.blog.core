package org.wcdevs.blog.core.common.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wcdevs.blog.core.common.TestsUtil;
import org.wcdevs.blog.core.common.util.StringUtils;
import org.wcdevs.blog.core.persistence.comment.Comment;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.util.ClockUtil;

class CommentTransformerTest {
  private CommentTransformer transformer;

  @BeforeEach
  void setUp() {
    transformer = new CommentTransformer();
  }

  @Test
  void newEntityFromDto() {
    try (var mockedStringUtils = mockStatic(StringUtils.class);
         var mockedClockUtil = mockStatic(ClockUtil.class)) {
      var anchor = TestsUtil.aString();
      var body = TestsUtil.aString();
      var lastUpdated = TestsUtil.randomLocalDateTime();
      var publishedBy = TestsUtil.aString();
      var post = mock(Post.class);
      var parentComment = mock(Comment.class);

      mockedStringUtils.when(() -> StringUtils.slugFrom(any())).thenReturn(anchor);
      mockedClockUtil.when(ClockUtil::utcNow).thenReturn(lastUpdated);

      var dto = mock(CommentDto.class);
      when(dto.getBody()).thenReturn(body);
      when(dto.getPublishedBy()).thenReturn(publishedBy);
      when(dto.getPost()).thenReturn(post);
      when(dto.getParentComment()).thenReturn(parentComment);

      var comment = transformer.newEntityFromDto(dto);

      assertNotNull(comment);
      assertEquals(anchor, comment.getAnchor());
      assertEquals(body, comment.getBody());
      assertEquals(lastUpdated, comment.getLastUpdated());
      assertEquals(publishedBy, comment.getPublishedBy());
      assertEquals(post, comment.getPost());
      assertEquals(parentComment, comment.getParentComment());
    }
  }

  @Test
  void dtoFromEntity() {
    var body = TestsUtil.aString();
    var publishedBy = TestsUtil.aString();
    var anchor = TestsUtil.aString();
    var lastUpdated = TestsUtil.randomLocalDateTime();

    var comment = mock(Comment.class);
    when(comment.getBody()).thenReturn(body);
    when(comment.getPublishedBy()).thenReturn(publishedBy);
    when(comment.getAnchor()).thenReturn(anchor);
    when(comment.getLastUpdated()).thenReturn(lastUpdated);

    var dto = transformer.dtoFromEntity(comment);

    assertNotNull(dto);
    assertEquals(body, dto.getBody());
    assertEquals(publishedBy, dto.getPublishedBy());
    assertEquals(anchor, dto.getAnchor());
    assertEquals(lastUpdated, dto.getLastUpdated());
  }

  @Test
  void update() {
    var body = TestsUtil.aString();
    var lastUpdated = TestsUtil.randomLocalDateTime();
    try (var mockedClockUtil = mockStatic(ClockUtil.class)) {
      mockedClockUtil.when(ClockUtil::utcNow).thenReturn(lastUpdated);
      var comment = mock(Comment.class);
      var dto = mock(CommentDto.class);
      when(dto.getBody()).thenReturn(body);

      transformer.update(comment, dto);

      verify(comment, times(1)).setBody(body);
      verify(comment, times(1)).setLastUpdated(lastUpdated);
    }
  }

  @Test
  void updateNonNullValuesNoNull() {
    var body = TestsUtil.aString();
    var lastUpdated = TestsUtil.randomLocalDateTime();
    try (var mockedClockUtil = mockStatic(ClockUtil.class)) {
      mockedClockUtil.when(ClockUtil::utcNow).thenReturn(lastUpdated);
      var comment = mock(Comment.class);
      var dto = mock(PartialCommentDto.class);
      when(dto.getBody()).thenReturn(body);

      transformer.updateNonNullValues(comment, dto);

      verify(comment, times(1)).setBody(body);
      verify(comment, times(1)).setLastUpdated(lastUpdated);
    }
  }

  @Test
  void updateNonNullValuesNullValues() {
    var lastUpdated = TestsUtil.randomLocalDateTime();
    try (var mockedClockUtil = mockStatic(ClockUtil.class)) {
      mockedClockUtil.when(ClockUtil::utcNow).thenReturn(lastUpdated);
      var comment = mock(Comment.class);
      var dto = mock(PartialCommentDto.class);
      when(dto.getBody()).thenReturn(null);

      transformer.updateNonNullValues(comment, dto);

      verify(comment, never()).setBody(any());
      verify(comment, times(1)).setLastUpdated(lastUpdated);
    }
  }
}
