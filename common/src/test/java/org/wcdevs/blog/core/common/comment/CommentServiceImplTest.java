package org.wcdevs.blog.core.common.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.wcdevs.blog.core.common.TestsUtil;
import org.wcdevs.blog.core.common.post.PostNotFoundException;
import org.wcdevs.blog.core.persistence.comment.Comment;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.CommentRepository;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostRepository;

@SpringBootTest(classes = {CommentService.class, CommentServiceImpl.class})
class CommentServiceImplTest {
  @Autowired
  private CommentService commentService;

  @MockBean
  private CommentTransformer commentTransformer;

  @MockBean
  private CommentRepository commentRepository;
  @MockBean
  private PostRepository postRepository;

  private static Stream<Arguments> createCommentArgs() {
    var parentCommentAnchor = TestsUtil.aString();
    var parentComment = mock(Comment.class);
    when(parentComment.getAnchor()).thenReturn(parentCommentAnchor);
    return Stream.of(arguments((Comment) null), arguments(parentComment));
  }

  @ParameterizedTest
  @MethodSource("createCommentArgs")
  void createComment(Comment parentComment) {
    var uuiStub = Optional.of(UUID.randomUUID());
    var parentCommentAnchor = parentComment != null ? parentComment.getAnchor() : null;

    var postSlug = TestsUtil.aString();
    var post = mock(Post.class);

    var dtoArg = mock(CommentDto.class);
    when(dtoArg.getParentCommentAnchor()).thenReturn(parentCommentAnchor);

    var savedCommentAnchor = TestsUtil.aString();
    var savedComment = mock(Comment.class);
    when(savedComment.getAnchor()).thenReturn(savedCommentAnchor);

    when(postRepository.findPostUuidWithSlug(postSlug)).thenReturn(uuiStub);
    when(postRepository.getReferenceById(uuiStub.get())).thenReturn(post);
    when(commentRepository.getCommentUuidWithAnchor(parentCommentAnchor)).thenReturn(uuiStub);
    when(commentRepository.getReferenceById(uuiStub.get())).thenReturn(parentComment);
    when(commentRepository.save(savedComment)).thenReturn(savedComment);
    when(commentTransformer.newEntityFromDto(dtoArg)).thenReturn(savedComment);

    var returnedDto = commentService.createComment(postSlug, dtoArg);

    assertNotNull(returnedDto);
    verify(postRepository, times(1)).findPostUuidWithSlug(postSlug);
    verify(postRepository, times(1)).getReferenceById(uuiStub.get());
    verify(commentRepository, times(1)).getCommentUuidWithAnchor(parentCommentAnchor);
    verify(commentRepository, times(1)).getReferenceById(uuiStub.get());
    verify(dtoArg, times(1)).setPost(post);
    verify(dtoArg, times(1)).setParentComment(parentComment);
    verify(commentRepository, times(1)).save(savedComment);

    assertEquals(savedCommentAnchor, returnedDto.getAnchor());
  }

  @Test
  void createCommentThrowsPostNotFoundException() {
    var slug = TestsUtil.aString();
    when(postRepository.findPostUuidWithSlug(slug)).thenReturn(Optional.empty());

    assertThrows(PostNotFoundException.class,
                 () -> commentService.createComment(slug, mock(CommentDto.class)));
  }

  @Test
  void getComment() {
    var anchor = TestsUtil.aString();

    var expected = mock(CommentDto.class);
    when(commentRepository.findCommentWithAnchor(anchor)).thenReturn(expected);

    var actual = commentService.getComment(anchor);

    assertEquals(expected, actual);
  }

  @Test
  void getCommentThrowsCommentNotFoundException() {
    when(commentRepository.findCommentWithAnchor(any())).thenReturn(null);
    var anchor = TestsUtil.aString();

    assertThrows(CommentNotFoundException.class, () -> commentService.getComment(anchor));
  }

  @Test
  void updateComment() {
    var anchor = TestsUtil.aString();
    var user = TestsUtil.aString();
    var comment = mock(Comment.class);
    when(comment.getAnchor()).thenReturn(anchor);

    when(commentRepository.findByAnchorAndPublishedBy(anchor, user))
        .thenReturn(Optional.of(comment));

    var partialCommentDto = mock(PartialCommentDto.class);
    var actual = commentService.updateComment(anchor, partialCommentDto, user);

    assertNotNull(actual);
    assertEquals(anchor, actual.getAnchor());
    verify(commentTransformer, times(1)).updateNonNullValues(comment, partialCommentDto);
    verify(commentRepository, times(1)).save(comment);
  }

  @Test
  void updateCommentThrowsNotFoundException() {
    when(commentRepository.findByAnchorAndPublishedBy(any(), any())).thenReturn(Optional.empty());
    var anchor = TestsUtil.aString();
    var user = TestsUtil.aString();
    var dto = mock(PartialCommentDto.class);

    assertThrows(CommentNotFoundException.class,
                 () -> commentService.updateComment(anchor, dto, user));
  }

  @Test
  void deleteCommentByAnchorAndUser() {
    var anchor = TestsUtil.aString();
    var user = TestsUtil.aString();
    when(commentRepository.deleteByAnchorAndPublishedBy(anchor, user)).thenReturn(1);

    commentService.deleteComment(anchor, user);

    verify(commentRepository, times(1)).deleteByAnchorAndPublishedBy(anchor, user);
  }

  @Test
  void deleteCommentByUser() {
    var anchor = TestsUtil.aString();
    when(commentRepository.deleteByAnchor(anchor)).thenReturn(1);

    commentService.deleteComment(anchor);

    verify(commentRepository, times(1)).deleteByAnchor(anchor);
  }

  @Test
  void deleteCommentByAnchorAndUserThrowsNotFoundException() {
    when(commentRepository.deleteByAnchorAndPublishedBy(any(), any())).thenReturn(0);
    var anchor = TestsUtil.aString();
    var user = TestsUtil.aString();

    assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(anchor, user));
  }

  @Test
  void deleteCommentByAnchorThrowsNotFoundException() {
    when(commentRepository.deleteByAnchor(any())).thenReturn(0);
    var anchor = TestsUtil.aString();

    assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(anchor));
  }

  @Test
  void getAllPostComments() {
    var postSlug = TestsUtil.aString();
    var elements = IntStream.rangeClosed(0, new Random().nextInt(13))
                            .mapToObj(i -> mock(CommentDto.class))
                            .collect(Collectors.toList());
    var expected = TestsUtil.pageOf(elements);
    when(commentRepository.findAllCommentsWithPostSlug(eq(postSlug), any(Pageable.class)))
        .thenReturn(expected);

    var actual = commentService.getAllPostComments(postSlug, TestsUtil.pageable());
    assertEquals(expected, actual);
  }

  @Test
  void getRootPostComments() {
    var postSlug = TestsUtil.aString();
    var elements = IntStream.rangeClosed(0, new Random().nextInt(13))
                            .mapToObj(i -> mock(CommentDto.class))
                            .collect(Collectors.toList());
    var expected = TestsUtil.pageOf(elements);
    when(commentRepository.findRootCommentsWithPostSlug(eq(postSlug), any(Pageable.class)))
        .thenReturn(expected);

    var actual = commentService.getRootPostComments(postSlug, TestsUtil.pageable());
    assertEquals(expected, actual);
  }

  @Test
  void getCommentChildComments() {
    var postSlug = TestsUtil.aString();
    var elements = IntStream.rangeClosed(0, new Random().nextInt(13))
                            .mapToObj(i -> mock(CommentDto.class))
                            .collect(Collectors.toList());
    var expected = TestsUtil.pageOf(elements);

    when(commentRepository.findChildCommentsWithParentAnchor(eq(postSlug), any(Pageable.class)))
        .thenReturn(expected);

    var actual = commentService.getParentCommentChildren(postSlug, TestsUtil.pageable());
    assertEquals(expected, actual);
  }
}
