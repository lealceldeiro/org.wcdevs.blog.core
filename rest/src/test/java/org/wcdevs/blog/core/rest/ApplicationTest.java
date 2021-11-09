package org.wcdevs.blog.core.rest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class ApplicationTest {
  @Test
  void applicationRunsSpringRunner() {
    try (var mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
      Application.main(new String[0]);
      mockedSpringApplication.verify(() -> SpringApplication.run(Application.class), Mockito.times(1));
    }
  }
}
