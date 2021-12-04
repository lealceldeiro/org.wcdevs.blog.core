package org.wcdevs.blog.core.common.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.wcdevs.blog.core.common.TestsUtil;
import static org.wcdevs.blog.core.common.TestsUtil.aString;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.persistence.post.PostRepository;

@SpringBootTest(classes = {PostService.class, PostServiceImpl.class})
class PostServiceImplTest {
  @Autowired
  private PostService postService;

  @MockBean
  private PostRepository postRepository;

  @Test
  void getPosts() {
    var expected = List.of(TestsUtil.buildDto(), TestsUtil.buildDto());
    when(postRepository.getPosts()).thenReturn(expected);

    var actual = postService.getPosts();

    assertEquals(expected, actual);
    verify(postRepository, times(1)).getPosts();
  }

  @Test
  void createPost() {
    var argMock = mock(PostDto.class);
    var postMock = mock(Post.class);
    var slugInfoMock = mock(PostDto.class);

    try (var mockedPostTransformer = mockStatic(PostTransformer.class)) {
      mockedPostTransformer.when(() -> PostTransformer.newEntityFromDto(argMock)).thenReturn(postMock);
      mockedPostTransformer.when(() -> PostTransformer.slugInfo(postMock)).thenReturn(slugInfoMock);
      when(postRepository.save(postMock)).thenReturn(postMock);

      var actual = postService.createPost(argMock);

      assertEquals(slugInfoMock, actual);
      verify(postRepository, times(1)).save(postMock);
      mockedPostTransformer.verify(() -> PostTransformer.newEntityFromDto(argMock), times(1));
      mockedPostTransformer.verify(() -> PostTransformer.slugInfo(postMock), times(1));
    }
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
    try (var mockedPostTransformer = mockStatic(PostTransformer.class)) {
      when(postRepository.findBySlug(slug)).thenReturn(Optional.of(post));
      mockedPostTransformer.when(() -> PostTransformer.dtoFromEntity(post))
                           .thenReturn(postDto);

      var actual = postService.getPost(slug);

      assertEquals(postDto, actual);
      verify(postRepository, times(1)).findBySlug(slug);
      mockedPostTransformer.verify(() -> PostTransformer.dtoFromEntity(post), times(1));
    }
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
    try (var mockedPostTransformer = mockStatic(PostTransformer.class)) {
      mockedPostTransformer.when(() -> PostTransformer.slugInfo(postMock)).thenReturn(slugInfoMock);

      var actual = postService.partialUpdate(slug, argMock);

      assertEquals(slugInfoMock, actual);

      verify(postRepository, times(1)).findBySlug(slug);
      verify(postMock, times(1)).setUpdatedOn(any(LocalDateTime.class));

      mockedPostTransformer
          .verify(() -> PostTransformer.updatePostWithNonNullValues(postMock, argMock), times(1));
      mockedPostTransformer.verify(() -> PostTransformer.slugInfo(postMock), times(1));
    }
  }

  @Test
  void fullyUpdatePost() {
    var argMock = mock(PostDto.class);
    var slug = aString();
    var postMock = mock(Post.class);
    var slugInfoMock = mock(PostDto.class);

    when(postRepository.findBySlug(slug)).thenReturn(Optional.of(postMock));
    try (var mockedPostTransformer = mockStatic(PostTransformer.class)) {
      mockedPostTransformer.when(() -> PostTransformer.slugInfo(postMock)).thenReturn(slugInfoMock);

      var actual = postService.fullUpdate(slug, argMock);

      assertEquals(slugInfoMock, actual);

      verify(postRepository, times(1)).findBySlug(slug);
      verify(postMock, times(1)).setUpdatedOn(any(LocalDateTime.class));

      mockedPostTransformer
          .verify(() -> PostTransformer.updatePost(postMock, argMock), times(1));
      mockedPostTransformer.verify(() -> PostTransformer.slugInfo(postMock), times(1));
    }
  }

  @Test
  void deletePostNotFound() {
    var slug = aString();
    when(postRepository.deleteBySlug(slug)).thenReturn(0);
    assertThrows(PostNotFoundException.class, () -> postService.deletePost(slug));
  }

  @Test
  void deletePost() {
    var slug = aString();
    when(postRepository.deleteBySlug(slug)).thenReturn(1);
    postService.deletePost(slug);

    verify(postRepository, times(1)).deleteBySlug(slug);
  }
}
