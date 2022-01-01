package org.wcdevs.blog.core.rest.auth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Extractor that works on fields from the {@link Jwt} authorization object by extracting certain
 * attributes.
 */
@Primary
@Component
@Qualifier("authJwtAttributeExtractor")
public class AuthJwtAttributeExtractor implements AuthAttributeExtractor {
  @Override
  @Nullable
  public <R> R extract(Object principal, String property) {
    if (principal instanceof Jwt) {
      var claims = ((Jwt) principal).getClaims();

      @SuppressWarnings("unchecked")
      var attr = (R) claims.get(property);
      return attr;
    }
    return null;
  }
}
