package com.wcdevs.blog.core.common.user;

import com.wcdevs.blog.core.persistence.user.UserDto;
import com.wcdevs.blog.core.persistence.user.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link UserService} implementation.
 */
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void createUser(UserDto userDto) {
    userRepository.save(UserTransformer.dtoToEntity(userDto));
  }
}
