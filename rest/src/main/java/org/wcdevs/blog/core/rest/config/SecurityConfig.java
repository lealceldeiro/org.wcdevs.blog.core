package org.wcdevs.blog.core.rest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.wcdevs.blog.core.rest.auth.JwtConverter;

/**
 * Security configuration class.
 */
@Component
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html
  static final String[] UNPROTECTED_GET_ENDPOINTS = new String[]{
      "/post/**",
      "/comment/**",
      "/docs/index.html"
  };

  private final JwtConverter jwtConverter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .cors().and()
        .authorizeRequests()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
        .antMatchers(HttpMethod.GET, UNPROTECTED_GET_ENDPOINTS).permitAll()
        .anyRequest().authenticated().and()
        .oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtConverter);
  }
}
