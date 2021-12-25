package org.wcdevs.blog.core.persistence.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  void dtoEqual() {
    var body = TestsUtil.aString();
    var dto1 = PartialCommentDto.builder().body(body).build();
    var dto2 = PartialCommentDto.builder().body(body).build();

    assertEquals(dto1, dto2);
  }

  @Test
  void dtoNotEqual() {
    var dto1 = PartialCommentDto.builder().body(TestsUtil.aString()).build();
    var dto2 = PartialCommentDto.builder().body(TestsUtil.aString()).build();

    assertNotEquals(dto1, dto2);
  }

  @Test
  void dtoToString() {
    var body = TestsUtil.aString();
    var dtoString = PartialCommentDto.builder().body(body).build().toString();

    assertTrue(dtoString.contains("body=" + body));
  }
}
