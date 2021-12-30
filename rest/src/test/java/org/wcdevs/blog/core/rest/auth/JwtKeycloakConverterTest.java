package org.wcdevs.blog.core.rest.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.wcdevs.blog.core.rest.TestsUtil.aString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class JwtKeycloakConverterTest {
  @Test
  void providerAuthorities() {
    // given
    JwtKeycloakConverter converter = new JwtKeycloakConverter();

    var rolePrefix = aString();

    var realmStub = Map.of(JwtKeycloakConverter.ROLES, List.of(aString(), aString()));
    Map<String, Object> claimsStub = Map.of(aString(), aString(),
                                            JwtKeycloakConverter.REALM_ACCESS, realmStub);
    var expected = realmStub.get(JwtKeycloakConverter.ROLES)
                            .stream()
                            .map(roleMock -> rolePrefix + roleMock)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());

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
}
