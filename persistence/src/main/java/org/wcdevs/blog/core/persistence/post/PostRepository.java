package org.wcdevs.blog.core.persistence.post;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository to handle the DB interaction with the "post" table.
 */
public interface PostRepository extends JpaRepository<Post, UUID> {
  @Query("select new org.wcdevs.blog.core.persistence.post.PostDto(p.title, p.slug, p.excerpt,"
         + "                                                       count(c)) "
         + "from Post p "
         + "left join Comment c on (c.post = p) "
         + "group by p.title, p.slug, p.excerpt")
  Page<PostDto> getPosts(Pageable pageable);

  Optional<Post> findBySlug(String slug);

  @Query("select p.uuid from Post p where p.slug = :slug")
  Optional<UUID> findPostUuidWithSlug(String slug);

  @Query("delete from Post p where p.slug = :slug")
  @Modifying
  int deleteBySlug(String slug);

  @Query("delete from Post p where p.slug = :slug and p.publishedBy = :user")
  @Modifying
  int deleteBySlugAndPublishedBy(String slug, String user);
}
