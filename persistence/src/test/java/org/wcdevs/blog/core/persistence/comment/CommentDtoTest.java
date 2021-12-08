package org.wcdevs.blog.core.persistence.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.wcdevs.blog.core.persistence.TestsUtil;

class CommentDtoTest {
  @Test
  void builder() {
    var anchor = TestsUtil.aString();
    var body = TestsUtil.aString();
    var publishedBy = TestsUtil.aString();
    var parentCommentAnchor = TestsUtil.aString();
    var postSlug = TestsUtil.aString();

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
                         .publishedBy(TestsUtil.aString())
                         .body(TestsUtil.aString())
                         .build();

    var dto2 = CommentDto.builder()
                         .anchor(anchor2)
                         .parentCommentAnchor(parentCommentAnchor2)
                         .postSlug(postSlug2)
                         .publishedBy(TestsUtil.aString())
                         .body(TestsUtil.aString())
                         .build();

    assertEquals(equal, dto1.equals(dto2));
  }

  @Test
  void toStringContainsFields() {
    var postSlug = TestsUtil.aString();
    var parentCommentAnchor = TestsUtil.aString();
    var publishedBy = TestsUtil.aString();
    var anchor = TestsUtil.aString();

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
}
