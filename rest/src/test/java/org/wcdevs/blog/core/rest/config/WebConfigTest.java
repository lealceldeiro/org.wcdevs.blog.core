package org.wcdevs.blog.core.rest.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

class WebConfigTest {
  @Test
  void addCorsMappings() {
    // given
    var random = new SecureRandom();
    var numberOfAllowedOrigins = random.nextInt(100);
    var allowedOrigins = IntStream.rangeClosed(0, numberOfAllowedOrigins + 1)
                                  .mapToObj(ignored -> UUID.randomUUID().toString())
                                  .collect(Collectors.toList());

    var coreProps = mock(CoreProperties.class);
    when(coreProps.getAllowedOrigins()).thenReturn(allowedOrigins);

    var corsRegistration = mock(CorsRegistration.class);

    var registry = mock(CorsRegistry.class);
    when(registry.addMapping(any())).thenReturn(corsRegistration);
    WebConfig webConfig = new WebConfig(coreProps);

    // when
    webConfig.addCorsMappings(registry);

    // then
    var webConfigResourcePathNumber = WebConfig.RESOURCE_PATTERNS.size();
    verify(registry, times(webConfigResourcePathNumber)).addMapping(any());
    verify(corsRegistration, times(webConfigResourcePathNumber)).allowedOriginPatterns(any());
  }
}
