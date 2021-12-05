package org.wcdevs.blog.core.persistence.post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository to handle the DB interaction with the "post" table.
 */
public interface PostRepository extends JpaRepository<Post, UUID> {
  @Query("select new org.wcdevs.blog.core.persistence.post.PostDto(p.title, p.slug, p.excerpt) "
         + "from Post p")
  List<PostDto> getPosts();

  Optional<Post> findBySlug(String slug);

  @Query("delete from Post p where p.slug = :slug")
  @Modifying
  int deleteBySlug(String slug);
}
