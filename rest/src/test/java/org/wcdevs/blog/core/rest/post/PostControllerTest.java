package org.wcdevs.blog.core.rest.post;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wcdevs.blog.core.rest.TestsUtil.MAPPER;
import static org.wcdevs.blog.core.rest.TestsUtil.aString;
import static org.wcdevs.blog.core.rest.TestsUtil.samplePostSlug;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.wcdevs.blog.core.common.comment.CommentService;
import org.wcdevs.blog.core.common.post.PostNotFoundException;
import org.wcdevs.blog.core.common.post.PostService;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.persistence.post.PostStatus;
import org.wcdevs.blog.core.rest.TestsUtil;
import org.wcdevs.blog.core.rest.auth.AuthAttributeExtractor;
import org.wcdevs.blog.core.rest.auth.Role;
import org.wcdevs.blog.core.rest.auth.SecurityContextAuthChecker;
import org.wcdevs.blog.core.rest.exceptionhandler.ControllerExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.ExceptionHandlerFactory;
import org.wcdevs.blog.core.rest.exceptionhandler.impl.ArgumentNotValidExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.impl.DataIntegrityViolationExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.impl.InvalidArgumentExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.impl.NotFoundExceptionHandler;

@EnableWebMvc
@EnableSpringDataWebSupport
@SpringBootTest(classes = {
    PostController.class, ControllerExceptionHandler.class, ExceptionHandlerFactory.class,
    NotFoundExceptionHandler.class, DataIntegrityViolationExceptionHandler.class,
    ArgumentNotValidExceptionHandler.class, InvalidArgumentExceptionHandler.class
})
@ExtendWith({SpringExtension.class})
class PostControllerTest {
  private static final String POST_URL = "/post";
  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;

  @MockBean
  private PostService postService;
  @MockBean
  private CommentService commentService;
  @MockBean
  private AuthAttributeExtractor authAttributeExtractor;
  @MockBean
  private SecurityContextAuthChecker securityContextAuthChecker;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(print()).build();

    when(postService.createPost(any(PostDto.class))).then(ignored -> TestsUtil.samplePostSlug());
    when(postService.getPost(anyString())).then(ignored -> TestsUtil.sampleFullPost());
    when(postService.partialUpdate(anyString(), any(PartialPostDto.class)))
        .then(ignored -> TestsUtil.samplePostSlug());
    when(postService.fullUpdate(anyString(), any(PostDto.class)))
        .then(ignored -> TestsUtil.samplePostSlug());

    when(authAttributeExtractor.principalUsername(any()))
        .thenReturn(TestsUtil.sampleFullPost().getPublishedBy());
  }

  @Test
  void getPosts() throws Exception {
    when(postService.getPosts(any(PostStatus.class), any(Pageable.class)))
        .then(ignored -> TestsUtil.pageOf(TestsUtil.samplePostsLiteData(PostStatus.PUBLISHED)));

    mockMvc.perform(get(POST_URL)).andExpect(status().isOk());
  }

  @Test
  void getPostsWithStatus() throws Exception {
    var status = PostStatus.PUBLISHED;
    when(postService.getPosts(eq(status), any(Pageable.class)))
        .then(ignored -> TestsUtil.pageOf(TestsUtil.samplePostsLiteData(status)));

    mockMvc.perform(get(POST_URL + "/status/{postStatus}", status)).andExpect(status().isOk());
  }

  @Test
  void createPost() throws Exception {
    mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(MAPPER.writeValueAsString(TestsUtil.samplePostTitleBody())))
           .andExpect(status().isCreated());
  }

  @Test
  void createDraft() throws Exception {
    var postDto = TestsUtil.builderFrom(TestsUtil.samplePostTitleBody()).status(null).build();
    var response = TestsUtil.builderFrom(samplePostSlug())
                            .slug(UUID.randomUUID().toString())
                            .build();
    when(postService.createPost(postDto)).thenReturn(response);

    mockMvc.perform(post(POST_URL + "/status/DRAFT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isCreated());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "",
      "-",
      "null",
      "ERROR: duplicate key value violates unique constraint. "
      + "Some other message will yield a not so well formatted response message",
      "ERROR: duplicate key value violates unique constraint. "
      + "Duplicate key value violates unique constraint. Values (slug)=(%s)",
      "ERROR: null value in column \"title\" violates not null constraint."
  })
  void createPostWithDataError(String rootCauseMsg) throws Exception {
    var postDto = TestsUtil.samplePostTitleBody();

    var rootCauseMessage = !"-".equals(rootCauseMsg)
                           ? (rootCauseMsg.contains("%s")
                              ? String.format(rootCauseMsg, postDto.getTitle())
                              : rootCauseMsg)
                           : null;
    var rootCause = mock(Throwable.class);
    when(rootCause.getMessage()).thenReturn(rootCauseMessage);

    var violationException = mock(DataIntegrityViolationException.class);
    when(violationException.getMessage()).thenReturn("Data Constraint Violation Exception");
    when(violationException.getRootCause()).thenReturn(rootCause);

    when(postService.createPost(postDto)).thenThrow(violationException);

    mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isConflict());
  }

  @Test
  void createPostWithBadFormatJson() throws Exception {
    var postDto = TestsUtil.samplePostTitleBody();

    mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("this_is_a_wrong_token_in_this_JSON_and_will_casue_an_error"
                                 + MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isBadRequest());
  }

  private static Stream<Arguments> createPostWithInvalidArgumentArgs() {
    var messageTitle = "Message title";
    var fullMessage = messageTitle + ";details";

    return Stream.of(arguments(new InvalidDataAccessApiUsageException(fullMessage), messageTitle));
  }

  @ParameterizedTest
  @MethodSource("createPostWithInvalidArgumentArgs")
  void createPostWithInvalidArgument(Throwable throwable, String expectedMessage) throws Exception {
    when(postService.createPost(any())).thenThrow(throwable);

    mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(MAPPER.writeValueAsString(TestsUtil.samplePostTitleBody())))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("message").value(expectedMessage));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "A very long value that cannot be a title. It should have been a value with length less than"
      + "or equal to the max allowed characters. Hence this title will cause the API to throw a bad"
      + "request exception informing the client about it. This is a sample title to show the error"
      + "handling and should not be emulated.",
      "a", ""
  })
  void createPostWithIncorrectTitle(String title) throws Exception {
    var postDto = TestsUtil.builderFrom(TestsUtil.samplePostTitleBody()).title(title).build();

    mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "a"})
  void createPostWithIncorrectBody(String body) throws Exception {
    var postDto = TestsUtil.builderFrom(TestsUtil.samplePostTitleBody()).body(body).build();

    mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "", "a",
      "a-very-long-slug-with-more-than-allowed-characters-to-show-the-api-error-handling-feature-"
      + "which-should-definitely-not-be-emulated-at-all-otherwise-an-error-will-be-reported-to-the-"
      + "calling-client-with-an-appropriate-message"
  })
  void createPostWithIncorrectSlug(String slug) throws Exception {
    var postDto = TestsUtil.builderFrom(TestsUtil.samplePostTitleBody()).slug(slug).build();

    mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isBadRequest());
  }

  @Test
  void getPost() throws Exception {
    var postDto = TestsUtil.samplePostSlug();

    mockMvc.perform(get(POST_URL + "/{postSlug}", postDto.getSlug())).andExpect(status().isOk());
  }

  @Test
  void getPostNotFound() throws Exception {
    var slug = samplePostSlug().getSlug();
    when(postService.getPost(slug)).thenThrow(new PostNotFoundException());

    mockMvc.perform(get(POST_URL + "/{postSlug}", slug)).andExpect(status().isNotFound());
  }

  @Test
  void partiallyUpdatePost() throws Exception {
    var postDto = TestsUtil.sampleFullPost();
    mockMvc.perform(patch(POST_URL + "/{postSlug}", postDto.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isOk());
  }

  @Test
  void fullyUpdatePost() throws Exception {
    var postDto = TestsUtil.sampleFullPost();

    mockMvc.perform(put(POST_URL + "/{postSlug}", postDto.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isOk());
  }

  @Test
  void deletePostByAuthor() throws Exception {
    var slug = TestsUtil.samplePostSlug().getSlug();
    var user = aString();
    when(securityContextAuthChecker.hasAnyRole(Role.EDITOR)).thenReturn(false);
    when(authAttributeExtractor.principalUsername(any())).thenReturn(user);

    mockMvc.perform(delete(POST_URL + "/{postSlug}", slug)).andExpect(status().isNoContent());

    verify(postService, times(1)).deletePost(slug, user);
    verify(postService, never()).deletePost(slug);
  }

  @Test
  void deletePostByEditor() throws Exception {
    var slug = samplePostSlug().getSlug();
    when(securityContextAuthChecker.hasAnyRole(Role.EDITOR)).thenReturn(true);

    mockMvc.perform(delete(POST_URL + "/{postSlug}", slug)).andExpect(status().isNoContent());

    verify(postService, times(1)).deletePost(slug);
    verify(postService, never()).deletePost(eq(slug), any());
  }

  private static Stream<Arguments> createCommentArgs() {
    return Stream.of(arguments(TestsUtil.sampleRootComment()),
                     arguments(TestsUtil.sampleChildComment()));
  }

  @ParameterizedTest
  @MethodSource("createCommentArgs")
  void createComment(CommentDto dto) throws Exception {
    var post = TestsUtil.samplePostSlug();
    when(commentService.createComment(post.getSlug(), dto))
        .thenReturn(CommentDto.builder().anchor(dto.getAnchor()).build());

    mockMvc.perform(post(POST_URL + "/{postSlug}/comment", post.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(MAPPER.writeValueAsString(dto)))
           .andExpect(status().isCreated());
  }

  @Test
  void getRootPostComments() throws Exception {
    var comments = TestsUtil.pageOf(TestsUtil.sampleRootComments());
    var postSlug = TestsUtil.samplePostSlug().getSlug();
    when(commentService.getRootPostComments(eq(postSlug), any(Pageable.class)))
        .thenReturn(comments);

    mockMvc.perform(get(POST_URL + "/{postSlug}/comment/root", postSlug))
           .andExpect(status().isOk());
  }

  @Test
  void getAllPostComments() throws Exception {
    var comments = TestsUtil.pageOf(TestsUtil.sampleComments());
    var postSlug = TestsUtil.samplePostSlug().getSlug();
    when(commentService.getAllPostComments(eq(postSlug), any(Pageable.class))).thenReturn(comments);

    mockMvc.perform(get(POST_URL + "/{postSlug}/comment/all", postSlug)).andExpect(status().isOk());
  }
}
