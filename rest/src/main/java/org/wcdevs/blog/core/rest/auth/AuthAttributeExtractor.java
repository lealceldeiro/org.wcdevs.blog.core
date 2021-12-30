package org.wcdevs.blog.core.rest.auth;

import org.springframework.lang.Nullable;

/**
 * Extractor that works on fields from the authorization object by extracting certain attributes.
 *
 * @param <T> Type of authorization object to work on.
 */
public interface AuthAttributeExtractor<T> {
  /**
   * Extract the specified {@code property} from {@code auth}.
   *
   * @param auth     Authorization object to extract the property from.
   * @param property Property to be extracted from the authorization object.
   * @param <R>      Type of the resulting extracted property. It must be the same type as the
   *                 stored property type.
   *
   * @return {@code property} value in {@code auth} or {@code null}.
   */
  @Nullable
  <R> R extract(T auth, String property);

  /**
   * Extract the principal username from {@code auth}.
   *
   * @param auth Authorization object to extract the property from.
   *
   * @return The principal username, stored in {@code auth}.
   */
  @Nullable
  default String principalUsername(T auth) {
    return extract(auth, ConverterUtil.PRINCIPAL_USERNAME);
  }
}
