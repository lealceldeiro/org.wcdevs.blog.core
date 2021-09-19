package com.wcdevs.blog.core.common.user;

import com.wcdevs.blog.core.persistence.user.User;
import com.wcdevs.blog.core.persistence.user.UserDto;

/**
 * Transformer for classes User and UserDto.
 */
final class UserTransformer {
  private UserTransformer() {
    // do not allow instantiation
  }

  /**
   * Creates a {@link User} from a {@link UserDto}'s data.
   *
   * @param dto {@link UserDto} instance.
   *
   * @return The newly created {@link User}.
   */
  static User dtoToEntity(UserDto dto) {
    return new User(
        dto.getUsername(),
        dto.getEmail(),
        dto.getPassword(),
        dto.getName(),
        dto.getLastName(),
        dto.getSignUpDate()
    );
  }
}
