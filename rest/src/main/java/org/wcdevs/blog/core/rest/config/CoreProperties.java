package org.wcdevs.blog.core.rest.config;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "core-app")
@Setter
@Getter
public class CoreProperties {
  /**
   * Origins allowed to perform requests to this server.
   */
  List<String> allowedOrigins = Collections.emptyList();
}
