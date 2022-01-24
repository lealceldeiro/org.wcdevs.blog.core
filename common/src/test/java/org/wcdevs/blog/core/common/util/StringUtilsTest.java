package org.wcdevs.blog.core.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.wcdevs.blog.core.common.TestsUtil;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class StringUtilsTest {

  @Test
  void slugFrom() {
    var slug = StringUtils.slugFrom("Some fancy title");
    assertTrue(slug.startsWith("some-fancy-title"));
  }

  @Test
  void emptyIfNullReturnsEmpty() {
    assertTrue(StringUtils.emptyIfNull(null).isEmpty());
  }

  @Test
  void emptyIfNullReturnsValue() {
    var value = TestsUtil.aString();
    assertEquals(value, StringUtils.emptyIfNull(value));
  }

  @Test
  void isUnfriendlySlugReturnsTrueIfNonUserFriendlyValue() {
    assertTrue(StringUtils.isUnfriendlySlug(UUID.randomUUID().toString()));
  }

  @Test
  void isUnfriendlySlugReturnsFalseIfUserFriendlyValue() {
    assertFalse(StringUtils.isUnfriendlySlug("a-string-a-bit-more-user-friendly"));
  }

  private static Stream<Arguments> isUuidArgs() {
    return Stream.of(arguments("some any string", false),
                     arguments(UUID.randomUUID().toString(), true));
  }

  @ParameterizedTest
  @MethodSource("isUuidArgs")
  void isUuid(String value, boolean isUuid) {
    assertEquals(isUuid, StringUtils.isUuid(value));
  }
}
