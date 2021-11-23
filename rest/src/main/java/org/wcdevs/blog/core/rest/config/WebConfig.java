package org.wcdevs.blog.core.rest.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
  private final CoreProperties coreProperties;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    log.info("Adding CORS allowed origins {}", coreProperties.getAllowedOrigins());
    coreProperties.getAllowedOrigins().forEach(registry::addMapping);
  }
}
