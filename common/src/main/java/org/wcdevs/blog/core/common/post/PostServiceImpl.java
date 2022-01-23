package org.wcdevs.blog.core.common.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wcdevs.blog.core.common.util.StringUtils;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.persistence.post.PostRepository;
import org.wcdevs.blog.core.persistence.post.PostStatus;

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
  public Page<PostDto> getPosts(PostStatus status, Pageable pageable) {
    return postRepository.getPosts(status.shortValue(), pageable);
  }

  @Override
  public PostDto createPost(PostDto postDto) {
    var post = postTransformer.newEntityFromDto(postDto);
    post = postDto.getStatus() == PostStatus.DRAFT ? saveAsDraft(post) : saveAsIs(post);

    return postTransformer.slugInfo(post);
  }

  private Post saveAsDraft(Post post) {
    var savedPost = postRepository.save(post);
    savedPost.setSlug(savedPost.getUuid().toString());

    return postRepository.save(savedPost);
  }

  private Post saveAsIs(Post post) {
    return postRepository.save(post);
  }

  @Override
  @Transactional(readOnly = true)
  public PostDto getPost(String postSlug) {
    var post = postRepository.findBySlug(postSlug).orElseThrow(PostNotFoundException::new);
    return postTransformer.dtoFromEntity(post);
  }

  @Override
  public PostDto partialUpdate(String postSlug, PartialPostDto newPostData) {
    var post = postRepository.findBySlug(postSlug).orElseThrow(PostNotFoundException::new);
    postTransformer.updateNonNullValues(post, newPostData);

    return postTransformer.slugInfo(post);
  }

  @Override
  public PostDto fullUpdate(String postSlug, PostDto newPostData) {
    var post = postRepository.findBySlug(postSlug).orElseThrow(PostNotFoundException::new);

    if (StringUtils.isUnfriendlySlug(newPostData.getSlug())
        && post.getStatus() == PostStatus.DRAFT) {
      newPostData.setSlug(StringUtils.slugFrom(newPostData.getTitle()));
    }

    postTransformer.update(post, newPostData);

    return postTransformer.slugInfo(post);
  }

  @Override
  public void deletePost(String postSlug) {
    if (postRepository.deleteBySlug(postSlug) < 1) {
      throw new PostNotFoundException();
    }
  }

  @Override
  public void deletePost(String postSlug, String user) {
    if (postRepository.deleteBySlugAndPublishedBy(postSlug, user) < 1) {
      throw new PostNotFoundException();
    }
  }
}
