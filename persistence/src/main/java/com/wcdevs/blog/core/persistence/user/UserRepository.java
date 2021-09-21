package com.wcdevs.blog.core.persistence.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to handle the DB interaction with the user table.
 */
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<UserDto> findByEmailOrUsername(String email, String username);
}
