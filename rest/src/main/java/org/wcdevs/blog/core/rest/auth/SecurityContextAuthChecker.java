package org.wcdevs.blog.core.rest.auth;

import static java.util.Arrays.stream;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Checks for criteria over the {@link SecurityContextHolder}.
 */
@Component
public class SecurityContextAuthChecker {
  /**
   * Checks if the {@link Authentication#getPrincipal()} is not null and has any of the
   * {@code roles}.
   *
   * @param roles {@link Role}s to be checked for presence in the authentication principal.
   *
   * @return {@code true} if the conditions described above are met, {@code false} otherwise.
   */
  public boolean hasAnyRole(Role... roles) {
    return hasAnyAuthority(stream(roles).map(Role::toString).toArray(String[]::new));
  }

  /**
   * Checks if the {@link Authentication#getPrincipal()} has any of the {@code authorities}.
   *
   * @param authorities Authorities to be checked for presence in the authentication principal.
   *
   * @return {@code true} if the conditions described above are met, {@code false} otherwise.
   */
  public boolean hasAnyAuthority(String... authorities) {
    var auth = SecurityContextHolder.getContext().getAuthentication();

    return Optional.ofNullable(auth)
                   .map(Authentication::getAuthorities)
                   .map(Collection::stream)
                   .orElse(Stream.empty())
                   .map(GrantedAuthority::getAuthority)
                   .anyMatch(cxtAuths -> stream(authorities).anyMatch(cxtAuths::contains));
  }
}
