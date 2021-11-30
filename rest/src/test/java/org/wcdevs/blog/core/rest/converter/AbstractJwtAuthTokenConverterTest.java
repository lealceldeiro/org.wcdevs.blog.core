package org.wcdevs.blog.core.rest.converter;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.wcdevs.blog.core.rest.TestsUtil;

class AbstractJwtAuthTokenConverterTest {
  @Test
  void convert() {
    // given
    Collection<GrantedAuthority> auth
        = new HashSet<>(Set.of(new SimpleGrantedAuthority(TestsUtil.aString()),
                               new SimpleGrantedAuthority(TestsUtil.aString())));
    try (var ignored = mockConstruction(
        JwtGrantedAuthoritiesConverter.class,
        (mock, context) -> when(mock.convert(any())).thenReturn(auth))
    ) {

      var jwtMock = mock(Jwt.class);
      when(jwtMock.getClaims()).thenReturn(emptyMap());

      var expected = new JwtAuthenticationToken(jwtMock, auth);

      // when
      var actual = new CognitoJwtAuthTokenConverter().convert(jwtMock);

      // then
      assertEquals(expected, actual);
    }
  }

  @Test
  void standardAuthorities() {
    // given
    Collection<GrantedAuthority> expected = Set.of(new SimpleGrantedAuthority(TestsUtil.aString()),
                                                   new SimpleGrantedAuthority(TestsUtil.aString()));
    try (var ignored = mockConstruction(
        JwtGrantedAuthoritiesConverter.class,
        (mock, context) -> when(mock.convert(any())).thenReturn(expected))
    ) {
      // when
      var actual = new CognitoJwtAuthTokenConverter().standardAuthorities(mock(Jwt.class));

      // then
      assertEquals(expected, actual);
    }
  }
}
