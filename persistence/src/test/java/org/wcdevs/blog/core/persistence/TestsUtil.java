package org.wcdevs.blog.core.persistence;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import org.wcdevs.blog.core.persistence.post.PostStatus;

public final class TestsUtil {
  private static final Random RANDOM = new SecureRandom();

  private TestsUtil() {
  }

  public static String aString() {
    return UUID.randomUUID().toString();
  }

  public static PostStatus aRandomPostStatus() {
    var values = PostStatus.values();
    return values[RANDOM.nextInt(values.length)];
  }
}
