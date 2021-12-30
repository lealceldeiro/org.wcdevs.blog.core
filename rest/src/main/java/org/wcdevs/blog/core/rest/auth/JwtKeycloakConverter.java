package org.wcdevs.blog.core.rest.auth;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
class JwtKeycloakConverter extends JwtAbstractConverter {
  static final String PREFERRED_USERNAME = "preferred_username";

  static final String REALM_ACCESS = "realm_access";
  static final String ROLES = "roles";

  @NonNull
  @Override
  protected Collection<GrantedAuthority> providerAuthorities(Jwt jwt) {
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

  @NonNull
  @Override
  protected Map<String, Object> customClaims(@NonNull Jwt jwt) {
    Object principalUsername = Optional.ofNullable(jwt.getClaim(PREFERRED_USERNAME))
                                       .map(JwtKeycloakConverter::toUsername)
                                       .orElse(ANONYMOUS);

    return Map.of(ConverterUtil.PRINCIPAL_USERNAME, principalUsername);
  }

  private static Object toUsername(@Nullable Object email) {
    return Optional.ofNullable(email)
                   .filter(String.class::isInstance)
                   .map(emailValue -> ((String) emailValue).split("@")[0])
                   .orElse(null);
  }
}
