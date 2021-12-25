package org.wcdevs.blog.core.rest.config;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CorePropertiesTest {
  @Test
  void allowedOrigins() {
    var allowedOrigins = List.of(UUID.randomUUID().toString());

    CoreProperties props = new CoreProperties();
    props.setAllowedOrigins(allowedOrigins);

    assertEquals(allowedOrigins, props.getAllowedOrigins());
  }
}
