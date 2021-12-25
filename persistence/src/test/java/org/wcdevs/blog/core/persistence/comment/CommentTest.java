package org.wcdevs.blog.core.persistence.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.wcdevs.blog.core.persistence.TestsUtil;
import org.wcdevs.blog.core.persistence.post.Post;

class CommentTest {
  @Test
  void equalsReturnsTrueForSameReference() {
    var comment = new Comment();
    assertTrue(comment.equals(comment));
  }

  @Test
  void equalsReturnsFalseIfOtherIsNull() {
    var comment = new Comment();
    assertFalse(comment.equals(null));
  }

  @Test
  void equalsReturnsFalseIfOtherIsFromDifferentClass() {
    try (var mockedHibernate = Mockito.mockStatic(Hibernate.class)) {
      mockedHibernate.when(() -> Hibernate.getClass(any()))
                     .thenReturn(Comment.class)
                     .thenReturn(String.class);
      var comment = new Comment();
      assertFalse(comment.equals(new Comment()));
    }
  }

  @Test
  void equalsReturnsTrueIfOtherUuidIsTheSame() {
    var uuid = UUID.randomUUID();

    var comment = new Comment();
    comment.setUuid(uuid);

    var other = new Comment();
    other.setUuid(uuid);

    assertTrue(comment.equals(other));
  }

  @Test
  void equalsReturnsFalseIfOtherUuidIsDifferent() {
    var comment = new Comment();
    comment.setUuid(UUID.randomUUID());

    var other = new Comment();
    other.setUuid(UUID.randomUUID());

    assertFalse(comment.equals(other));
  }

  @Test
  void equalsReturnsFalseIfUuidIsNull() {
    var comment = new Comment();
    comment.setUuid(null);

    var other = new Comment();
    other.setUuid(UUID.randomUUID());

    assertFalse(comment.equals(other));
  }

  @Test
  void hashCodeIsDerivedFromClass() {
    assertEquals(Comment.class.hashCode(), new Comment().hashCode());
  }

  @Test
  void testAccessors() {
    var uuid = UUID.randomUUID();
    var anchor = TestsUtil.aString();
    var body = TestsUtil.aString();
    var lastUpdated = LocalDateTime.now().minusDays(1);
    var publishedBy = TestsUtil.aString();
    var post = mock(Post.class);
    var parentComment = mock(Comment.class);

    var comment = new Comment();
    comment.setUuid(uuid);
    comment.setAnchor(anchor);
    comment.setBody(body);
    comment.setLastUpdated(lastUpdated);
    comment.setPublishedBy(publishedBy);
    comment.setPost(post);
    comment.setParentComment(parentComment);

    assertEquals(uuid, comment.getUuid());
    assertEquals(anchor, comment.getAnchor());
    assertEquals(body, comment.getBody());
    assertEquals(lastUpdated, comment.getLastUpdated());
    assertEquals(publishedBy, comment.getPublishedBy());
    assertEquals(post, comment.getPost());
    assertEquals(parentComment, comment.getParentComment());
  }

  private static Stream<Arguments> testDefinedConstructorArgs() {
    return Stream.of(arguments(new Comment()), arguments((Comment) null));
  }

  @ParameterizedTest
  @MethodSource("testDefinedConstructorArgs")
  void testDefinedConstructor(Comment parentComment) {
    var anchor = TestsUtil.aString();
    var body = TestsUtil.aString();
    var lastUpdated = LocalDateTime.now().minusDays(1);
    var publishedBy = TestsUtil.aString();
    var post = mock(Post.class);

    var comment = Objects.nonNull(parentComment)
                  ? new Comment(anchor, body, lastUpdated, publishedBy, post, parentComment)
                  : new Comment(anchor, body, lastUpdated, publishedBy, post);

    assertEquals(anchor, comment.getAnchor());
    assertEquals(body, comment.getBody());
    assertEquals(lastUpdated, comment.getLastUpdated());
    assertEquals(publishedBy, comment.getPublishedBy());
    assertEquals(post, comment.getPost());
    assertEquals(parentComment, comment.getParentComment());
  }

  @Test
  void toStringIncludesUuidAnchorAndPublishedBy() {
    var uuid = UUID.randomUUID();
    var anchor = UUID.randomUUID().toString();
    var publishedBy = UUID.randomUUID().toString();
    var comment = new Comment();

    comment.setUuid(uuid);
    comment.setAnchor(anchor);
    comment.setPublishedBy(publishedBy);

    var toString = comment.toString();

    assertTrue(toString.contains("publishedBy=" + publishedBy));
    assertTrue(toString.contains("anchor=" + anchor));
    assertTrue(toString.contains("uuid=" + uuid));
  }
}
