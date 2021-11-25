package org.wcdevs.blog.core.rest.converter;

import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toSet;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
class KeycloakJwtAuthTokenConverter implements JwtAuthTokenConverter {
  private static final String REALM_ACCESS = "realm_access";
  private static final String ROLES = "roles";
  private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter
      = new JwtGrantedAuthoritiesConverter();

  @Override
  public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
    Collection<GrantedAuthority> authorities = Optional.ofNullable(standardAuthorities(jwt))
                                                       .orElse(new HashSet<>());
    authorities.addAll(keyCloakAuthorities(jwt));

    return new JwtAuthenticationToken(jwt, authorities);
  }

  private Collection<GrantedAuthority> standardAuthorities(Jwt jwt) {
    return this.jwtGrantedAuthoritiesConverter.convert(jwt);
  }

  private Collection<GrantedAuthority> keyCloakAuthorities(Jwt jwt) {
    var claims = Optional.ofNullable(jwt.getClaims()).orElse(emptyMap());
    @SuppressWarnings("unchecked")
    var realmAccess = (Map<String, List<String>>) claims.getOrDefault(REALM_ACCESS, emptyMap());
    return Optional.ofNullable(realmAccess)
                   .orElse(emptyMap())
                   .getOrDefault(ROLES, emptyList())
                   .stream()
                   .map(KeycloakJwtAuthTokenConverter::toAuthRoleName)
                   .map(SimpleGrantedAuthority::new)
                   .collect(toSet());
  }

  private static String toAuthRoleName(String name) {
    return "ROLE_" + name;
  }
}
