package org.wcdevs.blog.core.rest;

import org.wcdevs.blog.core.common.util.StringUtils;

public final class TestMock {
  private TestMock() {
  }

  public static final String POST_TITLE1 = "Lorem ipsum dolor sit amet";
  public static final String POST_BODY1
      = "Curabitur ullamcorper ex at turpis fermentum, nec leo facilisis.";
  public static final String POST_EXCERPT1 = "Ullamcorper ex turpis fermentum leo.";

  public static final String POST_TITLE2 = "Integer in pretium turpis.";
  public static final String POST_BODY2
      = "Class aptent taciti sociosqu ad torquent per nostra, per himenaeos.";
  public static final String POST_EXCERPT2 = "Aptent taciti sociosqu ad per nostra.";

  public static final String POST_TITLE3 = "Fusce eu sagittis tortor.";
  public static final String POST_BODY3
      = "Orci varius natoque penatibus et magnis dis parturient montes.";
  public static final String POST_EXCERPT3 = "Penatibus et magnis dis parturient montes.";

  public static String slug1() {
    return slug(POST_TITLE1);
  }

  public static String slug2() {
    return slug(POST_TITLE2);
  }

  public static String slug3() {
    return slug(POST_TITLE3);
  }

  private static String slug(String s) {
    return StringUtils.slugFrom(s);
  }
}
