package org.wcdevs.blog.core.persistence.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.wcdevs.blog.core.persistence.TestsUtil;

class PostTest {
  @Test
  void equalsReturnsTrueForSameReference() {
    var post = new Post();
    assertTrue(post.equals(post));
  }

  @Test
  void equalsReturnsFalseIfOtherIsNull() {
    var post = new Post();
    assertFalse(post.equals(null));
  }

  @Test
  void equalsReturnsFalseIfOtherIsFromDifferentClass() {
    try (var mockedHibernate = Mockito.mockStatic(Hibernate.class)) {
      mockedHibernate.when(() -> Hibernate.getClass(any()))
                     .thenReturn(Post.class)
                     .thenReturn(String.class);
      var post = new Post();
      assertFalse(post.equals(new Post()));
    }
  }

  @Test
  void equalsReturnsTrueIfOtherPostUuidIsTheSame() {
    var uuid = UUID.randomUUID();

    var post = new Post();
    post.setUuid(uuid);

    var other = new Post();
    other.setUuid(uuid);

    assertTrue(post.equals(other));
  }

  @Test
  void equalsReturnsFalseIfOtherPostUuidIsDifferent() {
    var post = new Post();
    post.setUuid(UUID.randomUUID());

    var other = new Post();
    other.setUuid(UUID.randomUUID());

    assertFalse(post.equals(other));
  }

  @Test
  void equalsReturnsFalseIfUuidIsNull() {
    var post = new Post();
    post.setUuid(null);

    var other = new Post();
    other.setUuid(UUID.randomUUID());

    assertFalse(post.equals(other));
  }

  @Test
  void hashCodeIsDerivedFromClass() {
    assertEquals(Post.class.hashCode(), new Post().hashCode());
  }

  @Test
  void testAccessors() {
    var uuid = UUID.randomUUID();
    var title = TestsUtil.aString();
    var slug = TestsUtil.aString();
    var body = TestsUtil.aString();
    var excerpt = TestsUtil.aString();
    var publishedOn = LocalDateTime.now().minusDays(1);
    var updatedOn = LocalDateTime.now();
    var publishedBy = TestsUtil.aString();
    var updatedBy = TestsUtil.aString();

    var post = new Post();
    post.setUuid(uuid);
    post.setTitle(title);
    post.setSlug(slug);
    post.setBody(body);
    post.setExcerpt(excerpt);
    post.setPublishedOn(publishedOn);
    post.setUpdatedOn(updatedOn);
    post.setPublishedBy(publishedBy);
    post.setUpdatedBy(updatedBy);

    assertEquals(uuid, post.getUuid());
    assertEquals(title, post.getTitle());
    assertEquals(slug, post.getSlug());
    assertEquals(body, post.getBody());
    assertEquals(excerpt, post.getExcerpt());
    assertEquals(publishedOn, post.getPublishedOn());
    assertEquals(updatedOn, post.getUpdatedOn());
    assertEquals(publishedBy, post.getPublishedBy());
    assertEquals(updatedBy, post.getUpdatedBy());
  }

  @Test
  void testDefinedConstructor() {
    var updatedOn = LocalDateTime.now();
    var publishedOn = LocalDateTime.now().minusDays(1);
    var excerpt = TestsUtil.aString();
    var body = TestsUtil.aString();
    var slug = TestsUtil.aString();
    var title = TestsUtil.aString();
    var publishedBy = TestsUtil.aString();
    var updatedBy = TestsUtil.aString();

    var post = Post.builder()
                   .title(title)
                   .slug(slug)
                   .body(body)
                   .excerpt(excerpt)
                   .publishedOn(publishedOn)
                   .updatedOn(updatedOn)
                   .publishedBy(publishedBy)
                   .updatedBy(updatedBy)
                   .status(PostStatus.PUBLISHED)
                   .build();

    assertEquals(title, post.getTitle());
    assertEquals(slug, post.getSlug());
    assertEquals(body, post.getBody());
    assertEquals(excerpt, post.getExcerpt());
    assertEquals(publishedOn, post.getPublishedOn());
    assertEquals(updatedOn, post.getUpdatedOn());
    assertEquals(PostStatus.PUBLISHED, post.getStatus());
  }

  @Test
  void toStringIncludesUuidSlugAndTitle() {
    var uuid = UUID.randomUUID();
    var title = UUID.randomUUID().toString();
    var slug = UUID.randomUUID().toString();
    var post = new Post();
    post.setUuid(uuid);
    post.setTitle(title);
    post.setSlug(slug);
    var toString = post.toString();
    assertTrue(toString.contains("slug=" + slug));
    assertTrue(toString.contains("title=" + title));
    assertTrue(toString.contains("uuid=" + uuid));
  }
}
