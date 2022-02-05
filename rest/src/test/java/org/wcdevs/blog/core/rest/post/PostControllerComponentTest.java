package org.wcdevs.blog.core.rest.post;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wcdevs.blog.core.rest.TestMock.COMMENT_BODY1;
import static org.wcdevs.blog.core.rest.TestMock.COMMENT_BODY2;
import static org.wcdevs.blog.core.rest.TestMock.POST_BODY1;
import static org.wcdevs.blog.core.rest.TestMock.POST_BODY2;
import static org.wcdevs.blog.core.rest.TestMock.POST_BODY3;
import static org.wcdevs.blog.core.rest.TestMock.POST_EXCERPT1;
import static org.wcdevs.blog.core.rest.TestMock.POST_EXCERPT2;
import static org.wcdevs.blog.core.rest.TestMock.POST_EXCERPT3;
import static org.wcdevs.blog.core.rest.TestMock.POST_TITLE1;
import static org.wcdevs.blog.core.rest.TestMock.POST_TITLE2;
import static org.wcdevs.blog.core.rest.TestMock.POST_TITLE3;
import static org.wcdevs.blog.core.rest.TestMock.anchor1;
import static org.wcdevs.blog.core.rest.TestMock.slug1;
import static org.wcdevs.blog.core.rest.TestMock.slug2;
import static org.wcdevs.blog.core.rest.TestMock.slug3;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.wcdevs.blog.core.persistence.comment.Comment;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.CommentRepository;
import org.wcdevs.blog.core.persistence.post.Post;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.persistence.post.PostRepository;
import org.wcdevs.blog.core.persistence.post.PostStatus;
import org.wcdevs.blog.core.persistence.util.ClockUtil;
import org.wcdevs.blog.core.rest.Application;
import org.wcdevs.blog.core.rest.DocUtil;
import org.wcdevs.blog.core.rest.TestMock;
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

@SpringBootTest(classes = {
    Application.class, PostController.class, ControllerExceptionHandler.class,
    ExceptionHandlerFactory.class, NotFoundExceptionHandler.class,
    DataIntegrityViolationExceptionHandler.class, ArgumentNotValidExceptionHandler.class,
    InvalidArgumentExceptionHandler.class
})
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class PostControllerComponentTest {
  private static final String POST_URL = "/post";

  @Value("${spring.jackson.date-format}")
  private String dateFormat;

  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;

  @Autowired
  private PostRepository postRepository;
  @Autowired
  private CommentRepository commentRepository;

  @MockBean
  private AuthAttributeExtractor authAttributeExtractor;
  @MockBean
  private SecurityContextAuthChecker securityContextAuthChecker;

  private String postAuthor;
  private String commentAuthor;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(documentationConfiguration(restDocumentation))
                             .alwaysDo(print())
                             .build();
    postAuthor = TestsUtil.randomUsername();
    commentAuthor = TestsUtil.randomUsername();
    when(authAttributeExtractor.principalUsername(any())).thenReturn(postAuthor);
  }

  @AfterEach
  void tearDown() {
    // Clean state after the test completed, but no @Transaction:
    // https://www.javacodegeeks.com/2011/12/spring-pitfalls-transactional-tests.html
    // see also https://stackoverflow.com/a/37414387/5640649 for a different approach
    postRepository.deleteAll(); // will delete also all comments
  }

  @Test
  void getPosts() throws Exception {
    // given
    var status = PostStatus.PUBLISHED;
    givenPostWith(POST_TITLE1, slug1(), POST_BODY1, POST_EXCERPT1, status);
    givenPostWith(POST_TITLE2, slug2(), POST_BODY2, POST_EXCERPT2, status);
    givenPostWith(POST_TITLE3, slug3(), POST_BODY3, POST_EXCERPT3, status);

    // when
    var resultActions = mockMvc.perform(get(POST_URL));

    // then
    resultActions.andExpect(status().isOk())
                 .andDo(document("get_posts",
                                 preprocessResponse(prettyPrint()),
                                 DocUtil.pageableFieldsWith(
                                     fieldWithPath("content.[]")
                                         .description("List of posts"),
                                     fieldWithPath("content.[*].title")
                                         .description("Post title"),
                                     fieldWithPath("content.[*].slug")
                                         .description("Post slug"),
                                     fieldWithPath("content.[*].excerpt")
                                         .description("An excerpt of the post content"),
                                     fieldWithPath("content.[*].status")
                                         .description("Current status of the post"),
                                     fieldWithPath("content.[*].publishedBy")
                                         .description("User who published the post"),
                                     fieldWithPath("content.[*].updatedBy")
                                         .description("User who last updated the post"),
                                     fieldWithPath("content.[*].publishedOn")
                                         .description("Date time (UTC) when the post was published (" + dateFormat + ")"),
                                     fieldWithPath("content.[*].updatedOn")
                                         .description("Date time (UTC) when the post was last updated (" + dateFormat + ")"),
                                     fieldWithPath("content.[*].commentsCount")
                                         .description("Number of comments published in each post")
                                                           )
                                )
                       );
  }

  @Test
  void getPostsWithStatus() throws Exception {
    // given
    var status = TestsUtil.aRandomPostStatus();
    givenPostWith(POST_TITLE1, slug1(), POST_BODY1, POST_EXCERPT1, status);
    givenPostWith(POST_TITLE2, slug2(), POST_BODY2, POST_EXCERPT2, status);
    givenPostWith(POST_TITLE3, slug3(), POST_BODY3, POST_EXCERPT3, status);

    // when
    var resultActions = mockMvc.perform(get(POST_URL + "/status/{postStatus}", status));

    // then
    resultActions.andExpect(status().isOk())
                 .andDo(document("get_posts_by_status",
                                 preprocessResponse(prettyPrint()),
                                 DocUtil.pageableFieldsWith(
                                     fieldWithPath("content.[]")
                                         .description("List of posts"),
                                     fieldWithPath("content.[*].title")
                                         .description("Post title"),
                                     fieldWithPath("content.[*].slug")
                                         .description("Post slug"),
                                     fieldWithPath("content.[*].excerpt")
                                         .description("An excerpt of the post content"),
                                     fieldWithPath("content.[*].status")
                                         .description("Current status of the post"),
                                     fieldWithPath("content.[*].publishedBy")
                                         .description("User who published the post"),
                                     fieldWithPath("content.[*].updatedBy")
                                         .description("User who last updated the post"),
                                     fieldWithPath("content.[*].publishedOn")
                                         .description("Date time (UTC) when the post was published (" + dateFormat + ")"),
                                     fieldWithPath("content.[*].updatedOn")
                                         .description("Date time (UTC) when the post was last updated (" + dateFormat + ")"),
                                     fieldWithPath("content.[*].commentsCount")
                                         .description("Number of comments published in each post")
                                                           )
                                )
                       );
  }

  private Post givenPostWith(String title, String slug, String body, String excerpt) {
    return givenPostWith(title, slug, body, excerpt, TestsUtil.aRandomPostStatus());
  }

  private Post givenPostWith(String title, String slug, String body, String excerpt, PostStatus s) {
    var post = Post.builder()
                   .title(title)
                   .slug(slug)
                   .body(body)
                   .excerpt(excerpt)
                   .publishedOn(ClockUtil.utcNow())
                   .updatedOn(ClockUtil.utcNow())
                   .updatedBy(postAuthor)
                   .publishedBy(postAuthor)
                   .build();
    post.setStatus(s);
    return postRepository.save(post);
  }

  @Test
  void createPost() throws Exception {
    // given
    // language=JSON
    var payload = "{\n"
                  + "  \"title\": \"" + POST_TITLE1 + "\",\n"
                  + "  \"body\": \"" + POST_BODY1 + "\",\n"
                  + "  \"status\": \"" + PostStatus.PUBLISHED + "\"\n"
                  + "}";

    // when
    var resultActions = mockMvc.perform(post(POST_URL)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding(StandardCharsets.UTF_8)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isCreated())
                 .andDo(document("create_post", preprocessRequest(prettyPrint()),
                                 preprocessResponse(prettyPrint()),
                                 requestFields(
                                     fieldWithPath("title")
                                         .description("Post title"),
                                     fieldWithPath("slug")
                                         .optional()
                                         .type(DocUtil.STRING_TYPE)
                                         .description("A custom slug"),
                                     fieldWithPath("body")
                                         .description("Body of the post"),
                                     fieldWithPath("excerpt")
                                         .optional()
                                         .type(DocUtil.STRING_TYPE)
                                         .description("A custom excerpt"),
                                     fieldWithPath("status")
                                         .description("Post status")
                                              ),
                                 responseFields(
                                     fieldWithPath("slug")
                                         .description("Post slug"),
                                     fieldWithPath("status")
                                         .description("Post status")
                                               )
                                )
                       );
  }

  @Test
  void createDraft() throws Exception {
    // given
    var optional = " Not mandatory while creating a draft.";

    // language=JSON
    var payload = "{\n"
                  + "  \"title\": \"\",\n"
                  + "  \"body\": \"A draft for an idea... but even without a title yet\"\n"
                  + "}";

    // when
    var resultActions = mockMvc.perform(post(POST_URL + "/status/DRAFT")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding(StandardCharsets.UTF_8)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isCreated())
                 .andDo(document("create_post_draft",
                                 preprocessRequest(prettyPrint()),
                                 preprocessResponse(prettyPrint()),
                                 requestFields(
                                     fieldWithPath("title").description("Post title." + optional),
                                     fieldWithPath("body").description("Post body." + optional)
                                              ),
                                 responseFields(
                                     fieldWithPath("slug")
                                         .description("A temporal (not URL friendly) autogenerated "
                                                      + "post slug. Used to identify it later"),
                                     fieldWithPath("status")
                                         .description("Post status")
                                               )
                                )
                       );
  }

  @Test
  void createPostWithDataError() throws Exception {
    // given
    var constraints = new ConstraintDescriptions(PostDto.class);
    var titleConstraints = constraints.descriptionsForProperty("title");
    var slugConstraints = constraints.descriptionsForProperty("slug");
    var bodyConstraints = constraints.descriptionsForProperty("body");
    var excerptConstraints = constraints.descriptionsForProperty("excerpt");
    var statusConstraints = constraints.descriptionsForProperty("status");

    var sameSlug = slug1(); // will throw uniqueness constraint violation
    givenPostWith(POST_TITLE1, sameSlug, POST_BODY1, POST_EXCERPT1);

    // language=JSON
    var payload = "{\n"
                  + "  \"title\": \"" + POST_TITLE1 + "\",\n"
                  + "  \"body\": \"" + POST_BODY2 + "\",\n"
                  + "  \"slug\": \"" + sameSlug + "\",\n"
                  + "  \"status\": \"" + PostStatus.PUBLISHED + "\"\n"
                  + "}";

    // when
    var resultActions = mockMvc.perform(post(POST_URL)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding(StandardCharsets.UTF_8)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isConflict())
                 .andDo(document("create_post_db_error",
                                 preprocessRequest(prettyPrint()),
                                 preprocessResponse(prettyPrint()),
                                 requestFields(
                                     fieldWithPath("title")
                                         .description("Post title " + titleConstraints),
                                     fieldWithPath("slug")
                                         .optional()
                                         .type(DocUtil.STRING_TYPE)
                                         .description("An optional and unique custom slug "
                                                      + slugConstraints),
                                     fieldWithPath("body")
                                         .description("Post body " + bodyConstraints),
                                     fieldWithPath("excerpt")
                                         .optional()
                                         .type(DocUtil.STRING_TYPE)
                                         .description("A custom excerpt " + excerptConstraints),
                                     fieldWithPath("status")
                                         .description("Status in which the post will be published."
                                                      + statusConstraints)
                                              ),
                                 responseFields(
                                     fieldWithPath("message").description("Error message"),
                                     fieldWithPath("context").description("Request context"),
                                     fieldWithPath("dateTime").description("Request date time")
                                               )
                                )
                       );
  }

  @Test
  void createPostWithBadFormatJson() throws Exception {
    // given
    var payload = "incorrect json value";

    // when
    var resultActions = mockMvc.perform(post(POST_URL)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding(StandardCharsets.UTF_8)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isBadRequest())
                 .andDo(document("create_post_bad_format",
                                 preprocessRequest(prettyPrint())
                                )
                       );
  }

  private static Stream<Arguments> createPostWithInvalidArgumentArgs() {
    var longTitle = "A very long value that cannot be a title. It should have been a value with "
                    + "length less than or equal to the max allowed characters. Hence this title "
                    + "will cause the API to throw a bad request exception informing the client "
                    + "about it. This is a sample title to show the error handling and should not "
                    + "be emulated.";
    var shortTitle = "a";
    var emptyTitle = "";
    var shortBody = "a";
    var emptyBody = "";

    return Stream.of(arguments(longTitle, POST_BODY1, null),
                     arguments(shortTitle, POST_BODY1, null),
                     arguments(emptyTitle, POST_BODY1, "create_post_wrong_title"),
                     arguments(POST_TITLE1, shortBody, null),
                     arguments(POST_TITLE1, emptyBody, "create_post_wrong_body"));
  }

  @ParameterizedTest
  @MethodSource("createPostWithInvalidArgumentArgs")
  void createPostWithInvalidArgument(String title, String body, String docId) throws Exception {
    // given
    // language=JSON
    var payload = "{\n"
                  + "  \"title\": \"" + title + "\",\n"
                  + "  \"body\": \"" + body + "\",\n"
                  + "  \"status\": \"" + PostStatus.PUBLISHED + "\"\n"
                  + "}";

    // when
    var resultActions = mockMvc.perform(post(POST_URL)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding(StandardCharsets.UTF_8)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isBadRequest());
    if (docId != null) {
      resultActions.andDo(document(docId,
                                   preprocessRequest(prettyPrint()),
                                   preprocessResponse(prettyPrint())
                                  )
                         );

    }
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "", "a",
      "a-very-long-slug-with-more-than-allowed-characters-to-show-the-api-error-handling-feature-"
      + "which-should-definitely-not-be-emulated-at-all-otherwise-an-error-will-be-reported-to-the-"
      + "calling-client-with-an-appropriate-message"
  })
  void createPostWithIncorrectSlug(String slug) throws Exception {
    // given
    // language=JSON
    var payload = "{\n"
                  + "  \"title\": \"" + POST_TITLE1 + "\",\n"
                  + "  \"body\": \"" + POST_BODY1 + "\",\n"
                  + "  \"slug\": \"" + slug + "\",\n"
                  + "  \"status\": \"" + PostStatus.PUBLISHED + "\"\n"
                  + "}";

    // when
    var resultActions = mockMvc.perform(post(POST_URL)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding(StandardCharsets.UTF_8)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isBadRequest())
                 .andDo(document("create_post_wrong_slug",
                                 preprocessRequest(prettyPrint()),
                                 preprocessResponse(prettyPrint())
                                )
                       );
  }

  @Test
  void getPost() throws Exception {
    // given
    var slug = slug1();
    givenPostWith(POST_TITLE1, slug, POST_BODY1, POST_EXCERPT1);

    // when
    var resultActions = mockMvc.perform(get(POST_URL + "/{postSlug}", slug));

    // then
    resultActions.andExpect(status().isOk())
                 .andDo(document("get_post",
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("postSlug")
                                         .description("Post slug generated during creation")
                                               ),
                                 responseFields(
                                     fieldWithPath("title")
                                         .description("Post title"),
                                     fieldWithPath("slug")
                                         .description("Post slug"),
                                     fieldWithPath("body")
                                         .description("Post body"),
                                     fieldWithPath("excerpt")
                                         .description("An excerpt of the post content"),
                                     fieldWithPath("status")
                                         .description("Current post status"),
                                     fieldWithPath("publishedOn")
                                         .description("Date time where the post was published"),
                                     fieldWithPath("updatedOn")
                                         .description("Date time where the post was last updated"),
                                     fieldWithPath("publishedBy")
                                         .description("Author of the post"),
                                     fieldWithPath("updatedBy")
                                         .description("Last user who edited the post")
                                               )
                                )
                       );
  }

  @Test
  void getPostNotFound() throws Exception {
    // given
    var nonExistentSlug = "some-non-existent-slug-" + System.currentTimeMillis();

    // when
    var resultActions = mockMvc.perform(get(POST_URL + "/{postSlug}", nonExistentSlug));

    // then
    resultActions.andExpect(status().isNotFound())
                 .andDo(document("get_post_not_found",
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("postSlug")
                                         .description("Post slug generated during creation")
                                               ),
                                 responseFields(
                                     fieldWithPath("message").description("Error message"),
                                     fieldWithPath("context").description("Request context"),
                                     fieldWithPath("dateTime").description("Request date time")
                                               )
                                )
                       );
  }

  @Test
  void partiallyUpdatePost() throws Exception {
    // given
    var slug = slug1();
    givenPostWith(POST_TITLE1, slug, POST_BODY1, POST_EXCERPT1);

    // language=JSON
    var payload = "{\n"
                  + "  \"title\": \"" + POST_TITLE2 + "\",\n"
                  + "  \"body\": \"" + POST_BODY2 + "\",\n"
                  + "  \"excerpt\": \"" + POST_EXCERPT2 + "\"\n"
                  + "}";

    // when
    var resultActions = mockMvc.perform(patch(POST_URL + "/{postSlug}", slug)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding(StandardCharsets.UTF_8)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isOk())
                 .andDo(document("partial_update_post",
                                 preprocessRequest(prettyPrint()),
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("postSlug")
                                         .description("Post slug generated during creation")
                                               ),
                                 requestFields(
                                     fieldWithPath("title")
                                         .description("Post title"),
                                     fieldWithPath("slug")
                                         .optional()
                                         .type(DocUtil.STRING_TYPE)
                                         .description("Post slug"),
                                     fieldWithPath("body")
                                         .description("Body of the post"),
                                     fieldWithPath("excerpt")
                                         .description("Post excerpt")
                                              ),
                                 responseFields(
                                     fieldWithPath("slug")
                                         .description("Post slug"),
                                     fieldWithPath("status")
                                         .description("Post status")
                                               )
                                )
                       );
  }

  @Test
  void fullyUpdatePost() throws Exception {
    // given
    var slug = slug1();
    givenPostWith(POST_TITLE1, slug, POST_BODY1, POST_EXCERPT1);

    // language=JSON
    var payload = "{\n"
                  + "  \"title\": \"" + POST_TITLE2 + "\",\n"
                  + "  \"slug\": \"" + slug + "\",\n"
                  + "  \"body\": \"" + POST_BODY2 + "\",\n"
                  + "  \"excerpt\": \"" + POST_EXCERPT2 + "\",\n"
                  + "  \"status\": \"" + PostStatus.PUBLISHED + "\"\n"
                  + "}";

    // when
    var resultActions = mockMvc.perform(put(POST_URL + "/{postSlug}", slug)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding(StandardCharsets.UTF_8)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isOk())
                 .andDo(document("full_update_post",
                                 preprocessRequest(prettyPrint()),
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("postSlug")
                                         .description("Post slug generated during creation")
                                               ),
                                 requestFields(
                                     fieldWithPath("title")
                                         .description("Post title"),
                                     fieldWithPath("slug")
                                         .description("Post slug"),
                                     fieldWithPath("body")
                                         .description("Body of the post"),
                                     fieldWithPath("excerpt")
                                         .description("Post excerpt"),
                                     fieldWithPath("status")
                                         .description("Status in which the post will be set. Set "
                                                      + "the same current post status to not change"
                                                      + " the post publication status. For posts "
                                                      + "in " + PostStatus.DRAFT + " status, this "
                                                      + "value can be set to any other status to "
                                                      + "move the post to a new status (like "
                                                      + PostStatus.PUBLISHED + ", for example)")
                                              ),
                                 responseFields(
                                     fieldWithPath("slug")
                                         .description("Post slug"),
                                     fieldWithPath("status")
                                         .description("Post status")
                                               )
                                )
                       );
  }

  @Test
  void deletePostByAuthor() throws Exception {
    // given
    var slug = slug1();
    givenPostWith(POST_TITLE1, slug, POST_BODY1, POST_EXCERPT1);

    // the user is not editor
    when(securityContextAuthChecker.hasAnyRole(Role.EDITOR)).thenReturn(false);
    when(authAttributeExtractor.principalUsername(any())).thenReturn(postAuthor);

    // when
    var resultActions = mockMvc.perform(delete(POST_URL + "/{postSlug}", slug));

    // then
    resultActions.andExpect(status().isNoContent())
                 .andDo(document("delete_post",
                                 pathParameters(
                                     parameterWithName("postSlug")
                                         .description("Post slug generated during creation"))
                                )
                       );
  }

  @Test
  void deletePostByEditor() throws Exception {
    // given
    var slug = slug1();
    givenPostWith(POST_TITLE1, slug, POST_BODY1, POST_EXCERPT1);

    // the user is an editor
    when(securityContextAuthChecker.hasAnyRole(Role.EDITOR)).thenReturn(true);
    when(authAttributeExtractor.principalUsername(any())).thenReturn(null);

    // when
    var resultActions = mockMvc.perform(delete(POST_URL + "/{postSlug}", slug));

    // then
    resultActions.andExpect(status().isNoContent());
  }

  @Test
  void createRootComment() throws Exception {
    // given
    var constraints = new ConstraintDescriptions(CommentDto.class);
    var bodyConstraints = constraints.descriptionsForProperty("body");

    var slug = slug1();
    givenPostWith(POST_TITLE1, slug, POST_BODY1, POST_EXCERPT1);

    // language=JSON
    var payload = "{\n"
                  + "  \"body\": \"" + COMMENT_BODY1 + "\"\n"
                  + "}";

    // when
    var resultActions = mockMvc.perform(post(POST_URL + "/{postSlug}/comment", slug)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding(StandardCharsets.UTF_8)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isCreated())
                 .andDo(document("create_root_comment",
                                 preprocessRequest(prettyPrint()),
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("postSlug")
                                         .description("The slug of the post where the comment is "
                                                      + "published")
                                               ),
                                 requestFields(
                                     fieldWithPath("body")
                                         .description("Content of the comment " + bodyConstraints)
                                              ),
                                 responseFields(
                                     fieldWithPath("anchor")
                                         .description("Comment anchor. This is generated during the"
                                                      + " creation of the comment and should be used"
                                                      + " later to identify and retrieve any "
                                                      + "information about it")
                                               )
                                )
                       );
  }

  @Test
  void createChildComment() throws Exception {
    // given
    var constraints = new ConstraintDescriptions(CommentDto.class);
    var bodyConstraints = constraints.descriptionsForProperty("body");

    var slug = slug1();
    var parentPost = givenPostWith(POST_TITLE1, slug, POST_BODY1, POST_EXCERPT1);

    var anchor = TestMock.anchor1();
    givenCommentWith(parentPost, anchor, COMMENT_BODY1);

    // language=JSON
    var payload = "{\n"
                  + "  \"body\": \"" + COMMENT_BODY2 + "\",\n"
                  + "  \"parentCommentAnchor\": \"" + anchor + "\"\n"
                  + "}";
    // when
    var resultActions = mockMvc.perform(post(POST_URL + "/{postSlug}/comment", slug)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding(StandardCharsets.UTF_8)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isCreated())
                 .andDo(document("create_child_comment",
                                 preprocessRequest(prettyPrint()),
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("postSlug")
                                         .description("The slug of the post where the comment is "
                                                      + "published")
                                               ),
                                 requestFields(
                                     fieldWithPath("body")
                                         .description("Content of the comment " + bodyConstraints),
                                     fieldWithPath("parentCommentAnchor")
                                         .description("Anchor of the parent comment [Must not be "
                                                      + "null when a child comment is created]")
                                              ),
                                 responseFields(
                                     fieldWithPath("anchor")
                                         .description("Comment anchor. This is generated during the"
                                                      + " creation of the comment and should be "
                                                      + "used later to identify and retrieve any "
                                                      + "information about it")
                                               )
                                )
                       );
  }

  private void givenCommentWith(Post parentPost, String anchor, String body) {
    var comment = new Comment();
    comment.setAnchor(anchor);
    comment.setBody(body);
    comment.setLastUpdated(ClockUtil.utcNow());
    comment.setPublishedBy(commentAuthor);
    comment.setPost(parentPost);
    commentRepository.save(comment);
  }

  @Test
  void getRootPostComments() throws Exception {
    // given
    var slug = slug1();
    var post = givenPostWith(POST_TITLE1, slug, POST_BODY1, POST_EXCERPT1);
    givenCommentWith(post, anchor1(), COMMENT_BODY1);

    // when
    var resultActions = mockMvc.perform(get(POST_URL + "/{postSlug}/comment/root", slug));

    // then
    resultActions.andExpect(status().isOk())
                 .andDo(document("get_root_comments",
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("postSlug")
                                         .description("The slug of the post where the comments are "
                                                      + "published")
                                               ),
                                 DocUtil.pageableFieldsWith(
                                     fieldWithPath("content.[]")
                                         .description("List of comments"),
                                     fieldWithPath("content.[*].anchor")
                                         .description("Comment anchor. This is generated during "
                                                      + "the creation of the comment and should be "
                                                      + "used later to identify and retrieve any "
                                                      + "information about the comment"),
                                     fieldWithPath("content.[*].body")
                                         .description("Content of the comment"),
                                     fieldWithPath("content.[*].publishedBy")
                                         .description("Author of the comment"),
                                     fieldWithPath("content.[*].lastUpdated")
                                         .description("Last time the comment was updated"),
                                     fieldWithPath("content.[*].childrenCount")
                                         .description("Number of comments which are children of "
                                                      + "this comment. Those comments (children) "
                                                      + "can be seen as the number of replies to "
                                                      + "the parent comment")
                                                           )
                                )
                       );
  }

  @Test
  void getAllPostComments() throws Exception {
    // given
    var slug = slug1();
    var post = givenPostWith(POST_TITLE1, slug, POST_BODY1, POST_EXCERPT1);
    givenCommentWith(post, anchor1(), COMMENT_BODY1);

    // when
    var resultActions = mockMvc.perform(get(POST_URL + "/{postSlug}/comment/all", slug));

    // then
    resultActions.andExpect(status().isOk())
                 .andDo(document("get_all_comments",
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("postSlug")
                                         .description("The slug of the post where the comments are "
                                                      + "published")
                                               ),
                                 DocUtil.pageableFieldsWith(
                                     fieldWithPath("content.[]")
                                         .description("List of comments"),
                                     fieldWithPath("content.[*].anchor")
                                         .description("Comment anchor. This is generated during "
                                                      + "the creation of the comment and should be "
                                                      + "used later to identify and retrieve any "
                                                      + "information about the comment"),
                                     fieldWithPath("content.[*].body")
                                         .description("Content of the comment"),
                                     fieldWithPath("content.[*].publishedBy")
                                         .description("Author of the comment"),
                                     fieldWithPath("content.[*].lastUpdated")
                                         .description("Last time the comment was updated"),
                                     fieldWithPath("content.[*].childrenCount")
                                         .description("Number of comments which are children of "
                                                      + "this comment. Those comments (children) "
                                                      + "can be seen as the number of replies to "
                                                      + "the parent comment")
                                                           )
                                )
                       );
  }
}
