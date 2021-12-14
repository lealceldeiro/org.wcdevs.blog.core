package org.wcdevs.blog.core.persistence.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.wcdevs.blog.core.persistence.TestsUtil;

class PartialCommentDtoTest {
  @Test
  void builder() {
    var body = TestsUtil.aString();

    var dto = PartialCommentDto.builder()
                               .body(body)
                               .build();

    assertNotNull(dto);
    assertEquals(body, dto.getBody());
  }

  @Test
  void dtoReliesOnDefaultEqualsImpl() {
    var dto1 = PartialCommentDto.builder()
                                .body(TestsUtil.aString())
                                .build();

    var dto2 = PartialCommentDto.builder()
                                .body(TestsUtil.aString())
                                .build();

    assertNotEquals(dto1, dto2);
  }
}
