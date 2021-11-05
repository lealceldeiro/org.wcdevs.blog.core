package org.wcdevs.blog.core.persistence;

import java.util.UUID;

public final class TestsUtil {
  private TestsUtil() {
  }
  public static String aString() {
    return UUID.randomUUID().toString();
  }
}
