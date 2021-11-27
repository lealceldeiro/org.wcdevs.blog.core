package org.wcdevs.blog.core.rest.converter;

import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toSet;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
class KeycloakJwtAuthTokenConverter extends AbstractJwtAuthTokenConverter {
  private static final String REALM_ACCESS = "realm_access";
  private static final String ROLES = "roles";

  @Override
  @NonNull
  protected Collection<GrantedAuthority> providerAuthorities(final Jwt jwt) {
    var claims = Optional.ofNullable(jwt.getClaims()).orElse(emptyMap());
    @SuppressWarnings("unchecked")
    var realmAccess = (Map<String, List<String>>) claims.getOrDefault(REALM_ACCESS, emptyMap());
    return Optional.ofNullable(realmAccess)
                   .orElse(emptyMap())
                   .getOrDefault(ROLES, emptyList())
                   .stream()
                   .map(ConverterUtil::toAuthRoleName)
                   .map(SimpleGrantedAuthority::new)
                   .collect(toSet());
  }
}
