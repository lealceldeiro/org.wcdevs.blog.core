package org.wcdevs.blog.core.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PersistenceConfig.class)
class PersistenceConfigTest {
  @Autowired
  private PersistenceConfig config;

  @Test
  void loadContextForPersistConfig() {
    assertNotNull(config);
  }
}
