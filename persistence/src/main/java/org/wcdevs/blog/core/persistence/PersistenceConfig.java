package org.wcdevs.blog.core.persistence;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Config class to allow components framework scan.
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.wcdevs.blog.core.persistence")
@EntityScan
public class PersistenceConfig {
}
