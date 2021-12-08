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

class PartialCommentDtoTest {
  @Test
  void builder() {
    var anchor = TestsUtil.aString();
    var body = TestsUtil.aString();

    var dto = PartialCommentDto.builder()
                               .anchor(anchor)
                               .body(body)
                               .build();

    assertNotNull(dto);
    assertEquals(anchor, dto.getAnchor());
    assertEquals(body, dto.getBody());
  }

  private static Stream<Arguments> dtoWithAnchorParentCommentAnchorAndPostSlugAreEqualArgs() {
    var anchorA = "anchorA";
    var anchorB = "anchorB";

    return Stream.of(arguments(anchorA, anchorA, true), // equals for the three of them matching
                     arguments(anchorA, anchorB, false));
  }

  @ParameterizedTest
  @MethodSource("dtoWithAnchorParentCommentAnchorAndPostSlugAreEqualArgs")
  void dtoWithAnchorParentCommentAnchorAndPostSlugAreEqual(String anchor1, String anchor2,
                                                           boolean equal) {
    var dto1 = PartialCommentDto.builder()
                                .anchor(anchor1)
                                .body(TestsUtil.aString())
                                .build();

    var dto2 = PartialCommentDto.builder()
                                .anchor(anchor2)
                                .body(TestsUtil.aString())
                                .build();

    assertEquals(equal, dto1.equals(dto2));
  }

  @Test
  void toStringContainsFields() {
    var anchor = TestsUtil.aString();

    var toString = PartialCommentDto.builder()
                             .anchor(anchor)
                             .build()
                             .toString();
    assertTrue(toString.contains("anchor=" + anchor));
  }
}
