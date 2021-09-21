package com.wcdevs.blog.core.persistence.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to handle the DB interaction with the user table.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<UserDto> findByEmailOrUsername(String email, String username);
}
