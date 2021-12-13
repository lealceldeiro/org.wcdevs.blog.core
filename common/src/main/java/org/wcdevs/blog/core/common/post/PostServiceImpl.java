package org.wcdevs.blog.core.common.post;

import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
  private final PostRepository postRepository;
  private final PostTransformer postTransformer;

  @Override
  @Transactional(readOnly = true)
  public List<PostDto> getPosts() {
    return postRepository.getPosts();
  }

  @Override
  public PostDto createPost(PostDto postDto) {
    Post post = postRepository.save(postTransformer.newEntityFromDto(postDto));
    return postTransformer.slugInfo(post);
  }

  @Override
  @Transactional(readOnly = true)
  public PostDto getPost(String postSlug) {
    Post post = postRepository.findBySlug(postSlug).orElseThrow(PostNotFoundException::new);
    return postTransformer.dtoFromEntity(post);
  }

  @Override
  public PostDto partialUpdate(String postSlug, PartialPostDto newPostData) {
    Post post = postRepository.findBySlug(postSlug).orElseThrow(PostNotFoundException::new);
    postTransformer.updatePostWithNonNullValues(post, newPostData);

    return postTransformer.slugInfo(post);
  }

  @Override
  public PostDto fullUpdate(String postSlug, PostDto newPostData) {
    Post post = postRepository.findBySlug(postSlug).orElseThrow(PostNotFoundException::new);
    postTransformer.updatePost(post, newPostData);

    return postTransformer.slugInfo(post);
  }

  @Override
  public void deletePost(String postSlug) {
    if (postRepository.deleteBySlug(postSlug) < 1) {
      throw new PostNotFoundException();
    }
  }
}
