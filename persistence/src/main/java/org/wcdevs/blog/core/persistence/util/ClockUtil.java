package org.wcdevs.blog.core.persistence.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class ClockUtil {
  private static final ZoneId UTC = ZoneId.of("UTC");

  private ClockUtil() {}

  public static LocalDateTime utcNow() {
    return LocalDateTime.now(UTC);
  }
}
