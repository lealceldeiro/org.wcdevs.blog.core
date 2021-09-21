package com.wcdevs.blog.core.rest;

import com.wcdevs.blog.core.common.user.UserService;
import com.wcdevs.blog.core.persistence.user.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller providing "index" webservices.
 */
@RestController
public class IndexController {
  private final UserService userService;

  public IndexController(final UserService userService) {
    this.userService = userService;
  }

  @GetMapping("user-info")
  public ResponseEntity<UserDto> index(@RequestParam String usernameOrEmail) {
    UserDto user = userService.getUserInfoByEmailOrUserName(usernameOrEmail);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }
}
