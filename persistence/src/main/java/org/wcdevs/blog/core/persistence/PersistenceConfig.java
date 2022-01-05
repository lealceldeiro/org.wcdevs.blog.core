package org.wcdevs.blog.core.persistence;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;

/**
 * Config class to allow components framework scan.
 */
@EntityScan
@Configuration
@EnableJpaRepositories(basePackages = "org.wcdevs.blog.core.persistence",
                       bootstrapMode = BootstrapMode.DEFERRED)
public class PersistenceConfig {
}
