package org.wcdevs.blog.core.rest.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.wcdevs.blog.core.rest.TestsUtil;

class AuthJwtAttributeExtractorTest {
  private AuthAttributeExtractor authAttributeExtractor;

  @BeforeEach
  void setUp() {
    authAttributeExtractor = new AuthJwtAttributeExtractor();
  }

  @Test
  void extractReturnsNullIfPrincipalIsNoJwtObject() {
    assertNull(authAttributeExtractor.extract("notAJwt", TestsUtil.aString()));
  }

  @Test
  void extractReturnsCorrectlyTheProperty() {
    var expectedValue = TestsUtil.aString();
    var property = TestsUtil.aString();

    var jwt = mock(Jwt.class);
    when(jwt.getClaims()).thenReturn(Map.of(property, expectedValue));

    var actual = authAttributeExtractor.extract(jwt, property);

    assertEquals(expectedValue, actual);
  }

  @Test
  void principalUsernameReturnsCorrectlyTheProperty() {
    var expectedValue = TestsUtil.aString();
    var property = ConverterUtil.PRINCIPAL_USERNAME;

    var jwt = mock(Jwt.class);
    when(jwt.getClaims()).thenReturn(Map.of(property, expectedValue));

    var actual = authAttributeExtractor.principalUsername(jwt);

    assertEquals(expectedValue, actual);
  }
}
