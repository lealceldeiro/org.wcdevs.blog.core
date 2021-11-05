package org.wcdevs.blog.core.common.post;

import java.util.List;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;

/**
 * Provides services to handle the business logic concerning the {@link Post}s data.
 */
public interface PostService {
  PostDto createPost(PostDto postDto);

  PostDto getPost(String postSlug);

  PostDto updatePost(String postSlug, PartialPostDto newPostDto);

  void deletePost(String postSlug);

  List<PostDto> getPosts();
}
