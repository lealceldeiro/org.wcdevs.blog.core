package org.wcdevs.blog.core.persistence.post;

import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.Hibernate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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
    var updatedOn = LocalDateTime.now();
    var publishedOn = LocalDateTime.now().minusDays(1);
    var body = TestsUtil.aString();
    var slug = TestsUtil.aString();
    var title = TestsUtil.aString();

    var post = new Post();
    post.setUuid(uuid);
    post.setUpdatedOn(updatedOn);
    post.setPublishedOn(publishedOn);
    post.setBody(body);
    post.setSlug(slug);
    post.setTitle(title);

    assertEquals(uuid, post.getUuid());
    assertEquals(updatedOn, post.getUpdatedOn());
    assertEquals(publishedOn, post.getPublishedOn());
    assertEquals(body, post.getBody());
    assertEquals(slug, post.getSlug());
    assertEquals(title, post.getTitle());
  }

  @Test
  void testDefinedConstructor() {
    var updatedOn = LocalDateTime.now();
    var publishedOn = LocalDateTime.now().minusDays(1);
    var body = TestsUtil.aString();
    var slug = TestsUtil.aString();
    var title = TestsUtil.aString();

    var post = new Post(title, slug, body, publishedOn, updatedOn);

    assertEquals(updatedOn, post.getUpdatedOn());
    assertEquals(publishedOn, post.getPublishedOn());
    assertEquals(body, post.getBody());
    assertEquals(slug, post.getSlug());
    assertEquals(title, post.getTitle());
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
