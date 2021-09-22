package com.wcdevs.blog.core.common.post;

import com.wcdevs.blog.core.persistence.post.PartialPostDto;
import com.wcdevs.blog.core.persistence.post.Post;
import com.wcdevs.blog.core.persistence.post.PostDto;

/**
 * Provides services to handle the business logic concerning the {@link Post}s data.
 */
public interface PostService {
  PartialPostDto createPost(PostDto postDto);

  PostDto getPost(String postSlug);

  PartialPostDto updatePost(String postSlug, PartialPostDto newPostDto);

  void deletePost(String postSlug);
}
