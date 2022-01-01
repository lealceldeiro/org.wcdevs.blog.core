package org.wcdevs.blog.core.rest.auth;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.wcdevs.blog.core.rest.TestsUtil.aString;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

class JwtAbstractConverterTest {
  @Test
  void convert() {
    // given
    Collection<GrantedAuthority> auth
        = new HashSet<>(Set.of(new SimpleGrantedAuthority(aString()),
                               new SimpleGrantedAuthority(aString())));
    try (var ignored = mockConstruction(JwtGrantedAuthoritiesConverter.class,
                                        (mck, ctx) -> when(mck.convert(any())).thenReturn(auth))) {

      var jwt = Jwt.withTokenValue(aString())
                   .claim(aString(), aString())
                   .issuedAt(Instant.now())
                   .expiresAt(Instant.now().plusSeconds(120))
                   .header(aString(), aString())
                   .build();

      var expected = new JwtAuthenticationToken(jwt, auth);

      // when
      var actual = new JwtCognitoConverter().convert(jwt);

      // then
      assertEquals(expected, actual);
    }
  }

  @Test
  void standardAuthorities() {
    // given
    Collection<GrantedAuthority> expected = Set.of(new SimpleGrantedAuthority(aString()),
                                                   new SimpleGrantedAuthority(aString()));
    try (var ignored = mockConstruction(
        JwtGrantedAuthoritiesConverter.class,
        (mock, context) -> when(mock.convert(any())).thenReturn(expected))
    ) {
      // when
      var actual = new JwtCognitoConverter().standardAuthorities(mock(Jwt.class));

      // then
      assertEquals(expected, actual);
    }
  }

  @Test
  void customClaimsReturnsEmptyMapByDefault() {
    var abstractConverter = new JwtAbstractConverter() {
      @NonNull
      @Override
      protected Collection<GrantedAuthority> providerAuthorities(Jwt ignored) {
        return emptyList();
      }
    };

    assertEquals(Collections.emptyMap(), abstractConverter.customClaims(mock((Jwt.class))));
  }
}
