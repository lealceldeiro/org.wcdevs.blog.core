package org.wcdevs.blog.core.common.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.wcdevs.blog.core.common.TestsUtil.aString;
import static org.wcdevs.blog.core.common.TestsUtil.pageable;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.wcdevs.blog.core.common.TestsUtil;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.persistence.post.PostRepository;
import org.wcdevs.blog.core.persistence.post.PostStatus;

@SpringBootTest(classes = {PostService.class, PostServiceImpl.class})
class PostServiceImplTest {
  @Autowired
  private PostService postService;

  @MockBean
  private PostRepository postRepository;

  @MockBean
  private PostTransformer postTransformer;

  @Test
  void getPosts() {
    var pageable = pageable();
    var expected = TestsUtil.pageOf(TestsUtil.buildDto(), TestsUtil.buildDto());
    when(postRepository.getPosts(anyShort(), any(Pageable.class))).thenReturn(expected);

    var actual = postService.getPosts(PostStatus.DRAFT, pageable);

    assertEquals(expected, actual);
    verify(postRepository, times(1)).getPosts(PostStatus.DRAFT.shortValue(), pageable);
  }

  @Test
  void createPost() {
    var argMock = mock(PostDto.class);
    var postMock = mock(Post.class);
    var slugInfoMock = mock(PostDto.class);

    when(postTransformer.newEntityFromDto(argMock)).thenReturn(postMock);
    when(postTransformer.slugInfo(postMock)).thenReturn(slugInfoMock);
    when(postRepository.save(postMock)).thenReturn(postMock);

    var actual = postService.createPost(argMock);

    assertEquals(slugInfoMock, actual);
    verify(postRepository, times(1)).save(postMock);
    verify(postTransformer, times(1)).newEntityFromDto(argMock);
    verify(postTransformer, times(1)).slugInfo(postMock);
  }

  @Test
  void getPostNotFound() {
    var slug = aString();
    when(postRepository.findBySlug(slug)).thenReturn(Optional.empty());
    Executable getPost = () -> postService.getPost(slug);

    assertThrows(PostNotFoundException.class, getPost);
  }

  @Test
  void getPost() {
    var slug = aString();
    var post = mock(Post.class);
    var postDto = mock(PostDto.class);
    when(postRepository.findBySlug(slug)).thenReturn(Optional.of(post));
    when(postTransformer.dtoFromEntity(post)).thenReturn(postDto);

    var actual = postService.getPost(slug);

    assertEquals(postDto, actual);
    verify(postRepository, times(1)).findBySlug(slug);
    verify(postTransformer, times(1)).dtoFromEntity(post);
  }

  @Test
  void updatePostNotFound() {
    when(postRepository.findBySlug(anyString())).thenReturn(Optional.empty());

    Executable updatePost = () -> postService.partialUpdate(aString(), mock(PartialPostDto.class));
    assertThrows(PostNotFoundException.class, updatePost);
  }

  @Test
  void partiallyUpdatePost() {
    var argMock = mock(PartialPostDto.class);
    var slug = aString();
    var postMock = mock(Post.class);
    var slugInfoMock = mock(PostDto.class);

    when(postRepository.findBySlug(slug)).thenReturn(Optional.of(postMock));
    when(postTransformer.slugInfo(postMock)).thenReturn(slugInfoMock);

    var actual = postService.partialUpdate(slug, argMock);

    assertEquals(slugInfoMock, actual);

    verify(postRepository, times(1)).findBySlug(slug);

    verify(postTransformer, times(1)).updateNonNullValues(postMock, argMock);
    verify(postTransformer, times(1)).slugInfo(postMock);
  }

  @Test
  void fullyUpdatePost() {
    var argMock = mock(PostDto.class);
    var slug = aString();
    var postMock = mock(Post.class);
    var slugInfoMock = mock(PostDto.class);

    when(postRepository.findBySlug(slug)).thenReturn(Optional.of(postMock));
    when(postTransformer.slugInfo(postMock)).thenReturn(slugInfoMock);

    var actual = postService.fullUpdate(slug, argMock);

    assertEquals(slugInfoMock, actual);

    verify(postRepository, times(1)).findBySlug(slug);

    verify(postTransformer, times(1)).update(postMock, argMock);
    verify(postTransformer, times(1)).slugInfo(postMock);
  }

  @Test
  void deletePostBySlugNotFound() {
    var slug = aString();
    when(postRepository.deleteBySlug(slug)).thenReturn(0);
    assertThrows(PostNotFoundException.class, () -> postService.deletePost(slug));
  }

  @Test
  void deletePostBySlug() {
    var slug = aString();
    when(postRepository.deleteBySlug(slug)).thenReturn(1);
    postService.deletePost(slug);

    verify(postRepository, times(1)).deleteBySlug(slug);
    verify(postRepository, never()).deleteBySlugAndPublishedBy(eq(slug), any());
  }

  @Test
  void deletePostBySlugAndUserNotFound() {
    var slug = aString();
    var user = aString();
    when(postRepository.deleteBySlugAndPublishedBy(slug, user)).thenReturn(0);
    assertThrows(PostNotFoundException.class, () -> postService.deletePost(slug, user));
  }

  @Test
  void deletePostBySlugAndUser() {
    var slug = aString();
    var user = aString();
    when(postRepository.deleteBySlugAndPublishedBy(slug, user)).thenReturn(1);
    postService.deletePost(slug, user);

    verify(postRepository, times(1)).deleteBySlugAndPublishedBy(slug, user);
    verify(postRepository, never()).deleteBySlug(slug);
  }
}
