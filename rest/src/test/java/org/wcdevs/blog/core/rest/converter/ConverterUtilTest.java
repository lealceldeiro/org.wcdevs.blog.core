package org.wcdevs.blog.core.rest.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.wcdevs.blog.core.rest.TestsUtil;

class ConverterUtilTest {

  @Test
  void toAuthRoleName() {
    var name = TestsUtil.aString();
    assertEquals(ConverterUtil.ROLE_PREFIX + name, ConverterUtil.toAuthRoleName(name));
  }
}
