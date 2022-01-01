package org.wcdevs.blog.core.rest.auth;

class ConverterUtil {
  static final String PRINCIPAL_USERNAME = "username";
  static final String ROLE_PREFIX = "ROLE_";

  private ConverterUtil() {
  }

  static String toAuthRoleName(String name) {
    return ROLE_PREFIX + name;
  }
}
