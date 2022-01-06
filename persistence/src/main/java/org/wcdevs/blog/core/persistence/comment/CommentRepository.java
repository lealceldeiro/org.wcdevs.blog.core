package org.wcdevs.blog.core.persistence.comment;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository to handle the DB interaction with the "comment" table.
 */
public interface CommentRepository extends JpaRepository<Comment, UUID> {
  @Query("select c.uuid from Comment c where c.anchor = :anchor")
  Optional<UUID> getCommentUuidWithAnchor(String anchor);

  Optional<Comment> findByAnchorAndPublishedBy(String anchor, String user);

  @Query("select new org.wcdevs.blog.core.persistence.comment.CommentDto("
         + "c.anchor, c.body, c.publishedBy, c.lastUpdated, count (childC.uuid)) "
         + "from Comment c "
         + "left join Comment childC on (c.uuid = childC.parentComment.uuid) "
         + "where c.anchor = :anchor "
         + "group by c.anchor, c.body, c.publishedBy, c.lastUpdated")
  CommentDto findCommentWithAnchor(String anchor);

  @Query("select new org.wcdevs.blog.core.persistence.comment.CommentDto("
         + "c.anchor, c.body, c.publishedBy, c.lastUpdated, count (childC.uuid)) "
         + "from Comment c "
         + "inner join c.post p "
         + "left join Comment childC on (c.uuid = childC.parentComment.uuid) "
         + "where p.slug = :slug "
         + "group by c.anchor, c.body, c.publishedBy, c.lastUpdated")
  Page<CommentDto> findAllCommentsWithPostSlug(String slug, Pageable pageable);

  @Query("select new org.wcdevs.blog.core.persistence.comment.CommentDto("
         + "c.anchor, c.body, c.publishedBy, c.lastUpdated, count (childC.uuid)) "
         + "from Comment c "
         + "inner join c.post p "
         + "left join c.parentComment pc "
         + "left join Comment childC on (c.uuid = childC.parentComment.uuid) "
         + "where p.slug = :slug and pc is null "
         + "group by c.anchor, c.body, c.publishedBy, c.lastUpdated")
  Page<CommentDto> findRootCommentsWithPostSlug(String slug, Pageable pageable);

  @Query("select new org.wcdevs.blog.core.persistence.comment.CommentDto("
         + "c.anchor, c.body, c.publishedBy, c.lastUpdated, count (childC.uuid)) "
         + "from Comment c "
         + "inner join c.parentComment pc "
         + "left join Comment childC on (c.uuid = childC.parentComment.uuid) "
         + "where pc is not null and pc.anchor = :anchor "
         + "group by c.anchor, c.body, c.publishedBy, c.lastUpdated")
  Page<CommentDto> findChildCommentsWithParentAnchor(String anchor, Pageable pageable);

  @Query("delete from Comment c where c.anchor = :anchor")
  @Modifying
  int deleteByAnchor(String anchor);

  @Query("delete from Comment c where c.anchor = :anchor and c.publishedBy = :user")
  @Modifying
  int deleteByAnchorAndPublishedBy(String anchor, String user);
}
