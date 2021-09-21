package com.wcdevs.blog.core.common.user;

import com.wcdevs.blog.core.persistence.user.UserDto;

/**
 * Provides services to handle the business logic concerning the
 * {@link com.wcdevs.blog.core.persistence.user.User}s data.
 */
public interface UserService {
  UserDto getUserInfoByEmailOrUserName(String usernameOrEmail);
}
