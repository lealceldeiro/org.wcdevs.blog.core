package com.wcdevs.blog.core.common.post;

import com.wcdevs.blog.core.persistence.post.PartialPostDto;
import com.wcdevs.blog.core.persistence.post.Post;
import com.wcdevs.blog.core.persistence.post.PostDto;
import com.wcdevs.blog.core.persistence.post.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link PostService} implementation.
 */
@Service
@Transactional
public class PostServiceImpl implements PostService {
  private final PostRepository postRepository;

  public PostServiceImpl(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Override
  public PartialPostDto createPost(PostDto postDto) {
    Post post = postRepository.save(PostTransformer.entityFromDto(postDto));
    return PostTransformer.slugInfo(post);
  }

  @Override
  public PostDto getPost(String postSlug) {
    return PostTransformer.dtoFromEntity(postRepository.findBySlug(postSlug)
                                                       .orElseThrow(PostNotFoundException::new));
  }

  @Override
  public PartialPostDto updatePost(String postSlug, PartialPostDto newPost) {
    Post post = postRepository.findBySlug(postSlug).orElseThrow(PostNotFoundException::new);
    PostTransformer.updatePostWithNonNullValues(post, newPost);
    return PostTransformer.slugInfo(post);
  }

  @Override
  public void deletePost(String postSlug) {
    if (postRepository.deleteBySlug(postSlug) < 1) {
      throw new PostNotFoundException();
    }
  }
}
