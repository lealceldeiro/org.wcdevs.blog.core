package org.wcdevs.blog.core.rest.config;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration to be applied to web components.
 */
@Log4j2
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
  static final List<String> RESOURCE_PATTERNS = List.of("/**");
  private final CoreProperties coreProperties;

  @Override
  public void addCorsMappings(@NonNull CorsRegistry registry) {
    var origins = Optional.ofNullable(coreProperties.getAllowedOrigins())
                          .orElse(emptyList())
                          .toArray(new String[0]);
    log.info("Adding CORS allowed origins {} to {}", Arrays.toString(origins), RESOURCE_PATTERNS);

    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/cors/CorsConfiguration.html#setAllowedOriginPatterns-java.util.List-
    RESOURCE_PATTERNS.forEach(path -> registry.addMapping(path).allowedOriginPatterns(origins));
  }
}
