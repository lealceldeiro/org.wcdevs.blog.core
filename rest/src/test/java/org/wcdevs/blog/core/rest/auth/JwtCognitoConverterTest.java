package org.wcdevs.blog.core.rest.auth;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.wcdevs.blog.core.rest.TestsUtil.aString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.wcdevs.blog.core.rest.TestsUtil;

class JwtCognitoConverterTest {
  private static Stream<Arguments> providerAuthoritiesArgs() {
    return Stream.of(arguments(List.of(aString(), aString()), arguments(emptyList())),
                     arguments((List<String>) null));
  }

  @ParameterizedTest
  @MethodSource("providerAuthoritiesArgs")
  void providerAuthorities(List<String> groupsStub) {
    // given
    JwtCognitoConverter converter = new JwtCognitoConverter();

    Map<String, Object> claimsStub = new HashMap<>();
    claimsStub.put(aString(), aString());
    claimsStub.put(JwtCognitoConverter.COGNITO_GROUPS, groupsStub);

    @SuppressWarnings("unchecked")
    var expected = groupsStub != null
                   ? ((List<String>) claimsStub.get(JwtCognitoConverter.COGNITO_GROUPS))
                       .stream()
                       .map(roleMock -> Role.PREFIX + roleMock)
                       .map(SimpleGrantedAuthority::new)
                       .collect(Collectors.toSet())
                   : emptySet();

    var jwtMock = mock(Jwt.class);
    when(jwtMock.getClaims()).thenReturn(claimsStub);

    // when
    var actual = converter.providerAuthorities(jwtMock);

    // then
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void providerAuthoritiesClassCastException() {
    // given
    JwtCognitoConverter converter = new JwtCognitoConverter();

    var jwtMock = mock(Jwt.class);
    @SuppressWarnings("rawtypes")
    Map claims = new TreeMap(); // force cast error
    claims.put(1, aString());
    when(jwtMock.getClaims()).thenReturn(claims);

    // when
    var actual = converter.providerAuthorities(jwtMock);

    // then
    Assertions.assertEquals(emptySet(), actual);
  }

  @Test
  void customClaimsWithActualValue() {
    var username = TestsUtil.aString();

    var jwtMock = mock(Jwt.class);
    when(jwtMock.getClaim(JwtCognitoConverter.PREFERRED_USERNAME)).thenReturn(username);

    var actual = new JwtCognitoConverter().customClaims(jwtMock);

    assertEquals(Map.of(JwtConverter.PRINCIPAL_USERNAME, username), actual);
  }

  @Test
  void customClaimsWithAnonymousValue() {
    var jwtMock = mock(Jwt.class);
    when(jwtMock.getClaim(JwtCognitoConverter.PREFERRED_USERNAME)).thenReturn(null);

    var actual = new JwtCognitoConverter().customClaims(jwtMock);

    assertEquals(Map.of(JwtConverter.PRINCIPAL_USERNAME, JwtCognitoConverter.ANONYMOUS), actual);
  }
}
