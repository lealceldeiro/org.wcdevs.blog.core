package org.wcdevs.blog.core.rest.auth;

/**
 * Available user (principal) roles.
 */
public enum Role {
  ADMIN,
  EDITOR,
  AUTHOR,
  USER;

  public static final String PREFIX = "ROLE_";

  @Override
  public String toString() {
    return PREFIX + name();
  }

  public static String toAuthRoleName(String name) {
    return PREFIX + name;
  }
}
