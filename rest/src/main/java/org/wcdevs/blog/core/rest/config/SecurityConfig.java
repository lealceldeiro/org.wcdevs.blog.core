package org.wcdevs.blog.core.rest.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

@Component
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
        .cors().and()
        .authorizeRequests()
        .anyRequest().authenticated().and()
        .oauth2ResourceServer().jwt();
  }
}
