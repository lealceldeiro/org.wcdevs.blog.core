package org.wcdevs.blog.core.rest.auth;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("aws")
class JwtCognitoConverter extends JwtAbstractConverter {
  // https://docs.aws.amazon.com/cognito/latest/developerguide/role-based-access-control.html
  static final String COGNITO_GROUPS = "cognito:groups";

  @NonNull
  @Override
  protected Collection<GrantedAuthority> providerAuthorities(Jwt jwt) {
    var claims = Optional.ofNullable(jwt.getClaims()).orElse(emptyMap());
    List<String> cognitoGroups = cognitoGroupsFrom(claims);

    if (cognitoGroups.isEmpty()) {
      log.warn("Cognito groups are empty. This is probably an error or misconfiguration. claims {}",
               claims);
    }
    return cognitoGroups.stream()
                        .map(ConverterUtil::toAuthRoleName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(toSet());
  }

  private static List<String> cognitoGroupsFrom(Map<String, Object> claims) {
    List<String> defaultGroups = emptyList();
    try {
      @SuppressWarnings("unchecked")
      var groups = (List<String>) claims.getOrDefault(COGNITO_GROUPS, defaultGroups);
      return Optional.ofNullable(groups).orElse(defaultGroups);
    } catch (ClassCastException cce) {
      log.error("The claims map does not accept the '{}' string as a key.", COGNITO_GROUPS);
      return defaultGroups;
    }
  }

  @NonNull
  @Override
  protected Map<String, Object> customClaims(@NonNull Jwt jwt) {
    return Collections.emptyMap();
  }
}
