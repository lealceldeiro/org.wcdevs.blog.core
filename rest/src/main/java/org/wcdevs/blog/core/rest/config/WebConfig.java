package org.wcdevs.blog.core.rest.config;

import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
  private static final List<String> RESOURCE_PATTERNS = List.of("/**");
  private final CoreProperties coreProperties;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    var origins = Optional.ofNullable(coreProperties.getAllowedOrigins()).orElse(emptyList());
    log.info("Adding CORS allowed origins {} to {}", origins, RESOURCE_PATTERNS);
    RESOURCE_PATTERNS
        .forEach(path -> origins.forEach(registry.addMapping(path)::allowedOriginPatterns));
  }
}
