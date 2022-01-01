package org.wcdevs.blog.core.rest.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.wcdevs.blog.core.rest.auth.JwtConverter;

class SecurityConfigTest {
  @Test
  void configure() throws Exception {
    var httpSecurity = mock(HttpSecurity.class);

    var authorizationConfigurer = mock(ExpressionUrlAuthorizationConfigurer.AuthorizedUrl.class);
    var interceptUrlRegistry = mock(ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry.class);

    when(interceptUrlRegistry.requestMatchers(any())).thenReturn(authorizationConfigurer);
    when(interceptUrlRegistry.antMatchers(any(), any())).thenReturn(authorizationConfigurer);
    when(interceptUrlRegistry.anyRequest()).thenReturn(authorizationConfigurer);
    when(interceptUrlRegistry.and()).thenReturn(httpSecurity);

    when(authorizationConfigurer.authenticated()).thenReturn(interceptUrlRegistry);
    when(authorizationConfigurer.permitAll()).thenReturn(interceptUrlRegistry);

    var corsConfigurer = mock(CorsConfigurer.class);
    when(corsConfigurer.and()).thenReturn(httpSecurity);

    var jwtConfigurer = mock(OAuth2ResourceServerConfigurer.JwtConfigurer.class);
    when(jwtConfigurer.jwtAuthenticationConverter(any())).thenReturn(jwtConfigurer);

    var auth2ResourceServerConfigurer = mock(OAuth2ResourceServerConfigurer.class);
    when(auth2ResourceServerConfigurer.jwt()).thenReturn(jwtConfigurer);

    when(httpSecurity.cors()).thenReturn(corsConfigurer);
    when(httpSecurity.authorizeRequests()).thenReturn(interceptUrlRegistry);
    when(httpSecurity.oauth2ResourceServer()).thenReturn(auth2ResourceServerConfigurer);

    var jwtAuthTokenConverter = mock(JwtConverter.class);
    new SecurityConfig(jwtAuthTokenConverter).configure(httpSecurity);

    verify(httpSecurity, times(1)).cors();
    verify(httpSecurity, times(1)).authorizeRequests();
    verify(httpSecurity, times(1)).oauth2ResourceServer();

    verify(interceptUrlRegistry, times(1)).requestMatchers(any());
    verify(interceptUrlRegistry, times(1))
        .antMatchers(HttpMethod.GET, SecurityConfig.UNPROTECTED_GET_ENDPOINTS);
    verify(interceptUrlRegistry, times(1)).anyRequest();
    verify(authorizationConfigurer, times(1)).authenticated();
    verify(auth2ResourceServerConfigurer, times(1)).jwt();
    verify(jwtConfigurer, times(1)).jwtAuthenticationConverter(jwtAuthTokenConverter);
  }
}
