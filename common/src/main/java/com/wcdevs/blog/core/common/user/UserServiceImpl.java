package com.wcdevs.blog.core.common.user;

import com.wcdevs.blog.core.persistence.user.UserDto;
import com.wcdevs.blog.core.persistence.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link UserService} implementation.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDto getUserInfoByEmailOrUserName(String usernameOrEmail) {
    return userRepository.findByEmailOrUsernameAllIgnoreCase(usernameOrEmail, usernameOrEmail)
                         .orElseThrow(UserNotFoundException::new);
  }
}
