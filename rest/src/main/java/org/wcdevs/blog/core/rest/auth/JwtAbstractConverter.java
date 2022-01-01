package org.wcdevs.blog.core.rest.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

abstract class JwtAbstractConverter implements JwtConverter {
  private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter
      = new JwtGrantedAuthoritiesConverter();

  protected static final String ANONYMOUS = "anonymous";

  @Override
  public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
    Collection<GrantedAuthority> customAuthorities = standardAuthorities(jwt);
    customAuthorities.addAll(providerAuthorities(jwt));

    Map<String, Object> customClaims = new LinkedHashMap<>(jwt.getClaims());
    customClaims.putAll(customClaims(jwt));

    Jwt customJwt = new Jwt(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(),
                            jwt.getHeaders(), customClaims);

    return new JwtAuthenticationToken(customJwt, customAuthorities);
  }

  @NonNull
  protected Map<String, Object> customClaims(@NonNull Jwt jwt) {
    return Collections.emptyMap();
  }

  @NonNull
  protected final Collection<GrantedAuthority> standardAuthorities(Jwt jwt) {
    return Optional.ofNullable(jwtGrantedAuthoritiesConverter.convert(jwt)).orElse(new HashSet<>());
  }

  @NonNull
  protected abstract Collection<GrantedAuthority> providerAuthorities(Jwt jwt);
}
