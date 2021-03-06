package org.wcdevs.blog.core.common.util;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides utility methods for common string operations.
 */
public class StringUtils {
  private static final String SLUG_REPLACEMENT = "-";
  private static final String SLUG_REPLACE_REGEX = "[^a-z0-9]++";
  public static final int SLUG_MAX_LENGTH = 150;

  private StringUtils() {
    // do not instantiate
  }

  /**
   * Creates a slug-ish string from the input value.
   *
   * @param value The value to be "slugged".
   *
   * @return The "slugged" value.
   *
   * @see StringUtils#SLUG_REPLACE_REGEX
   */
  public static String slugFrom(String value) {
    var sanitized = Objects.requireNonNull(value)
                           .toLowerCase(Locale.ENGLISH)
                           .strip()
                           .replaceAll(SLUG_REPLACE_REGEX, SLUG_REPLACEMENT);
    var hashed = sanitized
                 + (!sanitized.endsWith(SLUG_REPLACEMENT) ? SLUG_REPLACEMENT : "")
                 + Math.abs(Objects.hash(sanitized));

    return sizedSlugFrom(hashed);
  }

  private static String sizedSlugFrom(String candidateSlug) {
    return candidateSlug.length() <= SLUG_MAX_LENGTH
           ? candidateSlug
           : candidateSlug.substring(candidateSlug.length() - SLUG_MAX_LENGTH);
  }

  public static String emptyIfNull(String value) {
    return Optional.ofNullable(value).orElse("");
  }


  /**
   * Returns whether a given slug is user-friendly or not.
   *
   * @param slug Slug to be checked.
   *
   * @return {@code false} if the slug is user-friendly, {@code false} otherwise.
   */
  public static boolean isUnfriendlySlug(String slug) {
    return slug == null || StringUtils.isUuid(slug);
  }

  /**
   * Returns whether a string value is an UUID or not.
   *
   * @param value Value to be checked.
   *
   * @return {@code true} if the value is a UUID as per the {@link UUID#toString()} spec or
   *         {@code false} otherwise.
   */
  public static boolean isUuid(String value) {
    try {
      UUID.fromString(value);
    } catch (IllegalArgumentException ignored) {
      return false;
    }
    return true;
  }
}
