package org.wcdevs.blog.core.rest.converter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

abstract class AbstractJwtAuthTokenConverter implements JwtAuthTokenConverter {
  private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter
      = new JwtGrantedAuthoritiesConverter();

  @Override
  public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
    Collection<GrantedAuthority> authorities = standardAuthorities(jwt);
    authorities.addAll(providerAuthorities(jwt));

    return new JwtAuthenticationToken(jwt, authorities);
  }

  @NonNull
  protected final Collection<GrantedAuthority> standardAuthorities(Jwt jwt) {
    return Optional.ofNullable(jwtGrantedAuthoritiesConverter.convert(jwt)).orElse(new HashSet<>());
  }

  @NonNull
  protected abstract Collection<GrantedAuthority> providerAuthorities(Jwt jwt);
}
