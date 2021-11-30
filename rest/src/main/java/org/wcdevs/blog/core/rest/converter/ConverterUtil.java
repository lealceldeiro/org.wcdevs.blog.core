package org.wcdevs.blog.core.rest.converter;

class ConverterUtil {
  static final String ROLE_PREFIX = "ROLE_";

  private ConverterUtil() {
  }

  static String toAuthRoleName(String name) {
    return ROLE_PREFIX + name;
  }
}
