package org.wcdevs.blog.core.persistence.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.wcdevs.blog.core.persistence.TestsUtil.aString;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.wcdevs.blog.core.persistence.post.Post;

class CommentDtoTest {
  @Test
  void builder() {
    var anchor = aString();
    var body = aString();
    var publishedBy = aString();
    var parentCommentAnchor = aString();
    var postSlug = aString();

    var dto = CommentDto.builder()
                        .anchor(anchor)
                        .body(body)
                        .publishedBy(publishedBy)
                        .parentCommentAnchor(parentCommentAnchor)
                        .build();

    assertNotNull(dto);
    assertEquals(anchor, dto.getAnchor());
    assertEquals(body, dto.getBody());
    assertEquals(publishedBy, dto.getPublishedBy());
    assertEquals(parentCommentAnchor, dto.getParentCommentAnchor());
  }

  private static Stream<Arguments> dtoWithAnchorParentCommentAnchorAreEqualArgs() {
    var anchorA = "anchorA";
    var anchorB = "anchorB";
    var parentCommentAnchorA = "parentCommentAnchorA";
    var parentCommentAnchorB = "parentCommentAnchorB";

    return Stream.of(arguments(anchorA, parentCommentAnchorA,
                               anchorA, parentCommentAnchorA,
                               true), // equals for the 2 of them matching
                     arguments(anchorA, parentCommentAnchorA,
                               anchorB, parentCommentAnchorA,
                               false),
                     arguments(anchorA, parentCommentAnchorA,
                               anchorA, parentCommentAnchorB,
                               false));
  }

  @ParameterizedTest
  @MethodSource("dtoWithAnchorParentCommentAnchorAreEqualArgs")
  void dtoWithAnchorParentCommentAnchorAreEqual(String anchor1, String parentCommentAnchor1,
                                                String anchor2, String parentCommentAnchor2,
                                                boolean equal) {
    var dto1 = CommentDto.builder()
                         .anchor(anchor1)
                         .parentCommentAnchor(parentCommentAnchor1)
                         .publishedBy(aString())
                         .body(aString())
                         .build();

    var dto2 = CommentDto.builder()
                         .anchor(anchor2)
                         .parentCommentAnchor(parentCommentAnchor2)
                         .publishedBy(aString())
                         .body(aString())
                         .build();

    assertEquals(equal, dto1.equals(dto2));
  }

  @Test
  void toStringContainsFields() {
    var postSlug = aString();
    var parentCommentAnchor = aString();
    var publishedBy = aString();
    var anchor = aString();

    var toString = CommentDto.builder()
                             .parentCommentAnchor(parentCommentAnchor)
                             .publishedBy(publishedBy)
                             .anchor(anchor)
                             .build()
                             .toString();
    assertTrue(toString.contains("anchor=" + anchor));
    assertTrue(toString.contains("parentCommentAnchor=" + parentCommentAnchor));
  }

  @Test
  void setters() {
    var parentComment = mock(Comment.class);
    var post = mock(Post.class);
    var publishedBy = aString();

    var dto = CommentDto.builder().build();
    dto.setParentComment(parentComment);
    dto.setPost(post);
    dto.setPublishedBy(publishedBy);

    assertNotNull(dto);
    assertEquals(parentComment, dto.getParentComment());
    assertEquals(post, dto.getPost());
    assertEquals(0, dto.getChildrenCount());
    assertEquals(publishedBy, dto.getPublishedBy());
  }

  @Test
  void newCommentDto() {
    var random = new Random();

    var body = aString();
    var publishedBy = aString();
    var anchor = aString();
    var lastUpdated = LocalDateTime.now().minusDays(random.nextInt(31));
    var childCount = random.nextLong();
    var commentDto = new CommentDto(anchor, body, publishedBy, lastUpdated, childCount);

    assertNotNull(commentDto);
    assertEquals(body, commentDto.getBody());
    assertEquals(publishedBy, commentDto.getPublishedBy());
    assertEquals(anchor, commentDto.getAnchor());
    assertEquals(lastUpdated, commentDto.getLastUpdated());
    assertEquals((int) childCount, commentDto.getChildrenCount());
  }
}
