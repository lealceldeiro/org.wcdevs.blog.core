package org.wcdevs.blog.core.common.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.persistence.post.PostStatus;

/**
 * Provides services to handle the business logic concerning the {@link Post}s data.
 */
public interface PostService {
  PostDto createPost(PostDto postDto);

  PostDto getPost(String postSlug);

  PostDto partialUpdate(String postSlug, PartialPostDto newPostDto);

  PostDto fullUpdate(String postSlug, PostDto newPostDto);

  void deletePost(String postSlug);

  void deletePost(String postSlug, String user);

  Page<PostDto> getPosts(PostStatus status, Pageable pageable);
}
