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
                        .postSlug(postSlug)
                        .build();

    assertNotNull(dto);
    assertEquals(anchor, dto.getAnchor());
    assertEquals(body, dto.getBody());
    assertEquals(publishedBy, dto.getPublishedBy());
    assertEquals(parentCommentAnchor, dto.getParentCommentAnchor());
    assertEquals(postSlug, dto.getPostSlug());
  }

  private static Stream<Arguments> dtoWithAnchorParentCommentAnchorAndPostSlugAreEqualArgs() {
    var anchorA = "anchorA";
    var anchorB = "anchorB";
    var parentCommentAnchorA = "parentCommentAnchorA";
    var parentCommentAnchorB = "parentCommentAnchorB";
    var postSlugA = "postSlugA";
    var postSlugB = "postSlugB";

    return Stream.of(arguments(anchorA, parentCommentAnchorA, postSlugA,
                               anchorA, parentCommentAnchorA, postSlugA,
                               true), // equals for the three of them matching
                     arguments(anchorA, parentCommentAnchorA, postSlugA,
                               anchorB, parentCommentAnchorA, postSlugA,
                               false),
                     arguments(anchorA, parentCommentAnchorA, postSlugA,
                               anchorA, parentCommentAnchorB, postSlugA,
                               false),
                     arguments(anchorA, parentCommentAnchorA, postSlugA,
                               anchorA, parentCommentAnchorA, postSlugB,
                               false));
  }

  @ParameterizedTest
  @MethodSource("dtoWithAnchorParentCommentAnchorAndPostSlugAreEqualArgs")
  void dtoWithAnchorParentCommentAnchorAndPostSlugAreEqual(String anchor1,
                                                           String parentCommentAnchor1,
                                                           String postSlug1,
                                                           String anchor2,
                                                           String parentCommentAnchor2,
                                                           String postSlug2, boolean equal) {
    var dto1 = CommentDto.builder()
                         .anchor(anchor1)
                         .parentCommentAnchor(parentCommentAnchor1)
                         .postSlug(postSlug1)
                         .publishedBy(aString())
                         .body(aString())
                         .build();

    var dto2 = CommentDto.builder()
                         .anchor(anchor2)
                         .parentCommentAnchor(parentCommentAnchor2)
                         .postSlug(postSlug2)
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
                             .postSlug(postSlug)
                             .parentCommentAnchor(parentCommentAnchor)
                             .publishedBy(publishedBy)
                             .anchor(anchor)
                             .build()
                             .toString();
    assertTrue(toString.contains("anchor=" + anchor));
    assertTrue(toString.contains("postSlug=" + postSlug));
    assertTrue(toString.contains("parentCommentAnchor=" + parentCommentAnchor));
  }

  @Test
  void setters() {
    var parentComment = mock(Comment.class);
    var post = mock(Post.class);
    var childrenCount = new SecureRandom().nextInt();

    var dto = CommentDto.builder().build();
    dto.setParentComment(parentComment);
    dto.setPost(post);
    dto.setChildrenCount(childrenCount);

    assertNotNull(dto);
    assertEquals(parentComment, dto.getParentComment());
    assertEquals(post, dto.getPost());
    assertEquals(childrenCount, dto.getChildrenCount());
  }

  @Test
  void newCommentDto() {
    var parentCommentAnchor = aString();
    var body = aString();
    var publishedBy = aString();
    var anchor = aString();
    var lastUpdated = LocalDateTime.now().minusDays(new Random().nextInt(31));
    var commentDto = new CommentDto(parentCommentAnchor, body, publishedBy, anchor, lastUpdated);

    assertNotNull(commentDto);
    assertEquals(parentCommentAnchor, commentDto.getParentCommentAnchor());
    assertEquals(body, commentDto.getBody());
    assertEquals(publishedBy, commentDto.getPublishedBy());
    assertEquals(anchor, commentDto.getAnchor());
    assertEquals(lastUpdated, commentDto.getLastUpdated());
  }
}
