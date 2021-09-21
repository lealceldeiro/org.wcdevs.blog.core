package com.wcdevs.blog.core.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 */
@SpringBootApplication(scanBasePackages = "com.wcdevs.blog.core")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
