package org.wcdevs.blog.core.rest.auth;

import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.wcdevs.blog.core.rest.TestsUtil;

class SecurityContextAuthCheckerTest {
  @Test
  void hasAnyRole() {
    try (var mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
      var expectedAuthorities = Role.values();
      // raw use of parameterized collection to workaround issue: argument mismatch;
      // Collection<capture#1 of ? extends GrantedAuthority>
      // cannot be converted to
      // Collection<capture#2 of ? extends GrantedAuthority>
      Collection simpleGrantedAuths = stream(expectedAuthorities).map(Objects::toString)
                                                                 .map(SimpleGrantedAuthority::new)
                                                                 .collect(Collectors.toSet());

      var authentication = mock(Authentication.class);
      when(authentication.getAuthorities()).thenReturn(simpleGrantedAuths);

      var context = mock(SecurityContext.class);
      when(context.getAuthentication()).thenReturn(authentication);

      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(context);

      var contextAuthChecker = new SecurityContextAuthChecker();

      stream(expectedAuthorities).forEach(auth -> assertTrue(contextAuthChecker.hasAnyRole(auth)));
    }
  }

  @Test
  void hasAnyAuthority() {
    try (var mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
      var random = new SecureRandom();

      var expectedAuthorities = IntStream.range(0, random.nextInt(13))
                                         .mapToObj(i -> TestsUtil.aString())
                                         .collect(Collectors.toSet());
      // raw use of parameterized collection to workaround issue: argument mismatch;
      // Collection<capture#1 of ? extends GrantedAuthority>
      // cannot be converted to
      // Collection<capture#2 of ? extends GrantedAuthority>
      Collection simpleGrantedAuths = expectedAuthorities.stream()
                                                         .map(SimpleGrantedAuthority::new)
                                                         .collect(Collectors.toSet());

      var authentication = mock(Authentication.class);
      when(authentication.getAuthorities()).thenReturn(simpleGrantedAuths);

      var context = mock(SecurityContext.class);
      when(context.getAuthentication()).thenReturn(authentication);

      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(context);

      var contextAuthChecker = new SecurityContextAuthChecker();

      expectedAuthorities.forEach(auth -> assertTrue(contextAuthChecker.hasAnyAuthority(auth)));
    }
  }
}
