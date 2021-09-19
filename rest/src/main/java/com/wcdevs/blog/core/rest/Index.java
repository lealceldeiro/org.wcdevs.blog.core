package com.wcdevs.blog.core.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller providing "index" webservices.
 */
@RestController
public class Index {
  @GetMapping
  public String index() {
    return "index";
  }
}
