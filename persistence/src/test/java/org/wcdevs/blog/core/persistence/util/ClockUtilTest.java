package org.wcdevs.blog.core.persistence.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class ClockUtilTest {
  @Test
  void utcNow() {
    // some nanoseconds will pass between computation of the first and second time
    assertEquals(LocalDateTime.now(ZoneId.of("UTC")).withNano(0).withSecond(0),
                 ClockUtil.utcNow().withNano(0).withSecond(0));
  }
}
