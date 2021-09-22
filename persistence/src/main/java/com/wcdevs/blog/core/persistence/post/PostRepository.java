package com.wcdevs.blog.core.persistence.post;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository to handle the DB interaction with the "post" table.
 */
public interface PostRepository extends JpaRepository<Post, UUID> {
  Optional<Post> findBySlug(String slug);

  @Query("delete from Post p where p.slug = :slug")
  @Modifying
  int deleteBySlug(String slug);
}
