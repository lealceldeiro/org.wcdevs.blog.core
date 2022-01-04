package org.wcdevs.blog.core.rest.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.wcdevs.blog.core.rest.TestsUtil;
import java.util.Arrays;
import java.util.Objects;

class RoleTest {
  @Test
  void toStringIsPrefixedProperly() {
    assertTrue(Arrays.stream(Role.values())
                     .map(Objects::toString)
                     .allMatch(value -> value.startsWith(Role.PREFIX)));
  }
  @Test
  void toAuthRoleName() {
    var name = TestsUtil.aString();
    assertEquals(Role.PREFIX + name, Role.toAuthRoleName(name));
  }
}
