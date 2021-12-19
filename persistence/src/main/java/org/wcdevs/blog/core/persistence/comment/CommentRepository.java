package org.wcdevs.blog.core.persistence.comment;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository to handle the DB interaction with the "comment" table.
 */
public interface CommentRepository extends JpaRepository<Comment, UUID> {
  @Query("select c.uuid from Comment c where c.anchor = :anchor")
  Optional<UUID> getCommentUuidWithAnchor(String anchor);

  Optional<Comment> findByAnchor(String anchor);

  @Query("select new org.wcdevs.blog.core.persistence.comment.CommentDto("
         + "c.parentComment,"
         + "c.body,"
         + "c.publishedBy,"
         + "c.anchor,"
         + "c.lastUpdated"
         + ") "
         + "from Comment c inner join Post p where p.slug = :postSlug")
  Set<CommentDto> findAllWithPostSlug(String postSlug);

  @Query("select new org.wcdevs.blog.core.persistence.comment.CommentDto("
         + "c.parentComment,"
         + "c.body,"
         + "c.publishedBy,"
         + "c.anchor,"
         + "c.lastUpdated"
         + ") "
         + "from Comment c inner join Post p where p.slug = :slug and c.parentComment is null")
  Set<CommentDto> findAllRootCommentsWithPostSlug(String slug);

  @Query("select new org.wcdevs.blog.core.persistence.comment.CommentDto("
         + "c.parentComment,"
         + "c.body,"
         + "c.publishedBy,"
         + "c.anchor,"
         + "c.lastUpdated"
         + ") "
         + "from Comment c where c.parentComment is not null and c.parentComment.anchor = :anchor")
  Set<CommentDto> findAllChildCommentsWithParentAnchor(String anchor);

  @Query("delete from Comment c where c.anchor = :anchor")
  @Modifying
  int deleteByAnchor(String anchor);
}
