package org.wcdevs.blog.core.rest.converter;

class ConverterUtil {
  private ConverterUtil() {
  }

  static String toAuthRoleName(String name) {
    return "ROLE_" + name;
  }
}
