package org.wcdevs.blog.core.rest.auth;

import org.springframework.lang.Nullable;

/**
 * Extractor that works on fields from the authorization object by extracting certain attributes.
 */
public interface AuthAttributeExtractor {
  /**
   * Extract the specified {@code property} from {@code principal}.
   *
   * @param principal Authorization object to extract the property from.
   * @param property  Property to be extracted from the authorization object.
   * @param <R>       Type of the resulting extracted property. It must be the same type as the
   *                  stored property type.
   *
   * @return {@code property} value in {@code principal} or {@code null}.
   */
  @Nullable
  <R> R extract(Object principal, String property);

  /**
   * Extract the principal username from {@code principal}.
   *
   * @param principal Authorization object to extract the property from.
   *
   * @return The principal username, stored in {@code principal}.
   */
  @Nullable
  default String principalUsername(Object principal) {
    return extract(principal, JwtConverter.PRINCIPAL_USERNAME);
  }
}
