package org.wcdevs.blog.core.common.post;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.persistence.post.PostRepository;

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
  @Transactional(readOnly = true)
  public List<PartialPostDto> getPosts() {
    return postRepository.getPosts();
  }

  @Override
  public PartialPostDto createPost(PostDto postDto) {
    Post post = postRepository.save(PostTransformer.entityFromDto(postDto));
    return PostTransformer.slugInfo(post);
  }

  @Override
  @Transactional(readOnly = true)
  public PostDto getPost(String postSlug) {
    Post post = postRepository.findBySlug(postSlug).orElseThrow(PostNotFoundException::new);
    return PostTransformer.dtoFromEntity(post);
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
