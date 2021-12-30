package org.wcdevs.blog.core.rest.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.wcdevs.blog.core.rest.TestsUtil.aString;

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

    var rolePrefix = aString();
    Map<String, Object> claimsStub = new HashMap<>();
    claimsStub.put(aString(), aString());
    claimsStub.put(JwtCognitoConverter.COGNITO_GROUPS, groupsStub);

    var expected = groupsStub != null
                   ? ((List<String>) claimsStub.get(JwtCognitoConverter.COGNITO_GROUPS))
                       .stream()
                       .map(roleMock -> rolePrefix + roleMock)
                       .map(SimpleGrantedAuthority::new)
                       .collect(Collectors.toSet())
                   : emptySet();

    var jwtMock = mock(Jwt.class);
    when(jwtMock.getClaims()).thenReturn(claimsStub);

    try (var mockedConverterUtil = mockStatic(ConverterUtil.class)) {
      mockedConverterUtil.when(() -> ConverterUtil.toAuthRoleName(anyString()))
                         .then(invocationOnMock -> rolePrefix + invocationOnMock.getArgument(0));
      // when
      var actual = converter.providerAuthorities(jwtMock);

      // then
      Assertions.assertEquals(expected, actual);
    }
  }

  @Test
  void providerAuthoritiesClassCastException() {
    // given
    JwtCognitoConverter converter = new JwtCognitoConverter();

    var jwtMock = mock(Jwt.class);
    Map claims = new TreeMap();// force cast error
    claims.put(1, aString());
    when(jwtMock.getClaims()).thenReturn(claims);

    // when
    var actual = converter.providerAuthorities(jwtMock);

    // then
    Assertions.assertEquals(emptySet(), actual);
  }
}
