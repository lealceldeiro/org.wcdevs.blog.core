package org.wcdevs.blog.core.rest.auth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Extractor that works on fields from the Jwt authorization object by extracting certain
 * attributes.
 *
 * @param <T> Type of Jwt authorization object to work on.
 */
@Primary
@Component
@Qualifier("jwtAttributeExtractor")
public class AuthJwtAttributeExtractor<T extends Jwt> implements AuthAttributeExtractor<T> {
  @Override
  @Nullable
  public <R> R extract(T authToken, String property) {
    if (authToken != null) {
      var claims = authToken.getClaims();

      @SuppressWarnings("unchecked")
      var attr = (R) claims.get(property);
      return attr;
    }
    return null;
  }
}
