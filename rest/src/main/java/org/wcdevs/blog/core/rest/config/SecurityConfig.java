package org.wcdevs.blog.core.rest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.wcdevs.blog.core.rest.converter.JwtAuthTokenConverter;

@Component
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html
  private static final String[] UNPROTECTED_GET_ENDPOINTS = new String[]{
      "/post/**",
      "/docs/index.html"
  };

  private final JwtAuthTokenConverter jwtAuthTokenConverter;

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
        .cors().and()
        .authorizeRequests()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
        .antMatchers(HttpMethod.GET, UNPROTECTED_GET_ENDPOINTS).permitAll()
        .anyRequest().authenticated().and()
        .oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthTokenConverter);
  }
}
