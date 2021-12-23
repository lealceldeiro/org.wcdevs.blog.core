package org.wcdevs.blog.core.rest.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wcdevs.blog.core.rest.TestsUtil.ERROR_RESPONSE_FIELDS;
import static org.wcdevs.blog.core.rest.TestsUtil.MAPPER;
import static org.wcdevs.blog.core.rest.TestsUtil.samplePostSlug;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.wcdevs.blog.core.common.post.PostNotFoundException;
import org.wcdevs.blog.core.common.post.PostService;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.rest.AppExceptionHandler;
import org.wcdevs.blog.core.rest.TestsUtil;
import org.wcdevs.blog.core.rest.errorhandler.ErrorHandlerFactory;
import org.wcdevs.blog.core.rest.errorhandler.impl.ArgumentNotValidExceptionHandler;
import org.wcdevs.blog.core.rest.errorhandler.impl.DataIntegrityViolationErrorHandler;
import org.wcdevs.blog.core.rest.errorhandler.impl.NotFoundErrorHandler;

@EnableWebMvc
@SpringBootTest(classes = {
    PostController.class, AppExceptionHandler.class, ErrorHandlerFactory.class,
    NotFoundErrorHandler.class, DataIntegrityViolationErrorHandler.class,
    ArgumentNotValidExceptionHandler.class
})
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class PostControllerTest {
  private static final String BASE_URL = "/post/";
  private static final PathParametersSnippet SLUG_PATH_PARAMETER
      = pathParameters(parameterWithName("postSlug")
                           .description("Post slug generated during creation"));
  private static final RequestFieldsSnippet REQUEST_FIELDS
      = requestFields(fieldWithPath("title").description("Post title. Mandatory, unique."),
                      fieldWithPath("slug").description("A custom slug. Optional, but unique."),
                      fieldWithPath("body").description("Body of the post. Mandatory."),
                      fieldWithPath("excerpt").description("A custom excerpt. Optional."),
                      fieldWithPath("publishedBy").description("Author of the post. Mandatory"),
                      fieldWithPath("updatedBy").description("Last user who edited the post. "
                                                             + "Optional (default: publishedBy)"),
                      fieldWithPath("publishedOn").ignored(),
                      fieldWithPath("updatedOn").ignored());
  private static final ResponseFieldsSnippet SLUG_INFO_RESPONSE_FIELDS
      = responseFields(
      fieldWithPath("slug")
          .description("Post slug. This value must be used to identify (and retrieve) the post "
                       + "later"));

  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;

  @MockBean
  private PostService postService;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(documentationConfiguration(restDocumentation))
                             .build();

    when(postService.createPost(any(PostDto.class))).
        then(ignored -> TestsUtil.samplePostSlug());
    when(postService.getPost(anyString())).then(ignored -> TestsUtil.sampleFullPost());
    when(postService.partialUpdate(anyString(), any(PartialPostDto.class)))
        .then(ignored -> TestsUtil.samplePostSlug());
    when(postService.fullUpdate(anyString(), any(PostDto.class)))
        .then(ignored -> TestsUtil.samplePostSlug());
    when(postService.getPosts()).then(ignored -> TestsUtil.samplePostSlugTitles());
  }

  @Test
  void getPosts() throws Exception {
    mockMvc.perform(get(BASE_URL))
           .andExpect(status().isOk())
           .andDo(document("get_posts",
                           responseFields(fieldWithPath("[]")
                                              .description("List of posts information"),
                                          fieldWithPath("[*].title")
                                              .description("Post title"),
                                          fieldWithPath("[*].slug")
                                              .description("Post slug. This value can be used to "
                                                           + "retrieve the post later"),
                                          fieldWithPath("[*].excerpt")
                                              .description("An excerpt of the post content")
                                         )
                          )
                 );
  }

  @Test
  void createPost() throws Exception {
    var postDto = TestsUtil.sampleFullPost();
    mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isCreated())
           .andDo(document("create_post", REQUEST_FIELDS, SLUG_INFO_RESPONSE_FIELDS));
  }

  @Test
  void createPostDBError() throws Exception {
    var postDto = TestsUtil.samplePostTitleBody();
    var err = String.format("There's already a post with title %s", postDto.getSlug());
    when(postService.createPost(postDto)).thenThrow(new DataIntegrityViolationException(err));
    mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isConflict());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "",
      "-",
      "null",
      "ERROR: duplicate key value violates unique constraint. "
      + "Some other message will yield a not so well formatted response message",
      "ERROR: duplicate key value violates unique constraint. "
      + "Duplicate key value violates unique constraint. Values (title)=(%s)"
  })
  void createPostDBErrorWithRootCause(String rootCauseMsg) throws Exception {
    var postDto = TestsUtil.sampleFullPost();

    var rootCauseMessage = !"-".equals(rootCauseMsg)
                           ? (rootCauseMsg.contains("%s")
                              ? String.format(rootCauseMsg, postDto.getTitle()) : rootCauseMsg)
                           : null;
    var rootCause = mock(Throwable.class);
    when(rootCause.getMessage()).thenReturn(rootCauseMessage);

    var violationException = mock(DataIntegrityViolationException.class);
    when(violationException.getMessage()).thenReturn("Data Constraint Violation Exception");
    when(violationException.getRootCause()).thenReturn(rootCause);

    when(postService.createPost(postDto)).thenThrow(violationException);

    mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isConflict())
           .andDo(document("create_post_db_error", REQUEST_FIELDS, ERROR_RESPONSE_FIELDS));
  }

  @Test
  void createPostWithBadFormatJson() throws Exception {
    var postDto = TestsUtil.samplePostTitleBody();

    mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("/" + MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isBadRequest())
           .andDo(document("create_post_bad_format"));
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
    var prototype = TestsUtil.sampleFullPost();
    var now = LocalDateTime.now();
    var postDto = new PostDto(title, prototype.getSlug(), prototype.getBody(),
                              prototype.getExcerpt(), prototype.getPublishedBy(),
                              prototype.getUpdatedBy(), now, now);

    mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isBadRequest())
           .andDo(document("create_post_wrong_title"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", ""})
  void createPostWithIncorrectBody(String body) throws Exception {
    var prototype = TestsUtil.sampleFullPost();
    var now = LocalDateTime.now();
    var postDto = new PostDto(prototype.getTitle(), prototype.getSlug(), body,
                              prototype.getExcerpt(), prototype.getPublishedBy(),
                              prototype.getUpdatedBy(), now, now);

    mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isBadRequest())
           .andDo(document("create_post_wrong_body"));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "", "a",
      "a-very-long-slug-with-more-than-allowed-characters-to-show-the-api-error-handling-feature-"
      + "which-should-definively-not-be-emulated-at-all-otherwise-an-error-will-be-reported-to-the-"
      + "calling-client-with-an-approriate-message"
  })
  void createPostWithIncorrectSlug(String slug) throws Exception {
    var prototype = TestsUtil.sampleFullPost();
    var now = LocalDateTime.now();
    var postDto = new PostDto(prototype.getTitle(), slug, prototype.getBody(),
                              prototype.getExcerpt(), prototype.getPublishedBy(),
                              prototype.getUpdatedBy(), now, now);

    mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isBadRequest())
           .andDo(document("create_post_wrong_slug"));
  }

  @Test
  void getPost() throws Exception {
    var postDto = TestsUtil.samplePostSlug();
    var responseFields = responseFields(
        fieldWithPath("title").description("Post title"),
        fieldWithPath("slug").description("Post slug. It can be used to retrieve the post later"),
        fieldWithPath("body").description("Post body"),
        fieldWithPath("excerpt").description("An excerpt of the post content"),
        fieldWithPath("publishedOn").description("Date time where the post was published"),
        fieldWithPath("updatedOn").description("Date time where the post was last updated"),
        fieldWithPath("publishedBy").description("Author of the post"),
        fieldWithPath("updatedBy").description("Last user who edited the post. ")
                                       );

    mockMvc.perform(get(BASE_URL + "{postSlug}", postDto.getSlug()))
           .andExpect(status().isOk())
           .andDo(document("get_post", SLUG_PATH_PARAMETER, responseFields));
  }

  @Test
  void getPostNotFound() throws Exception {
    var slug = samplePostSlug().getSlug();
    when(postService.getPost(slug)).thenThrow(new PostNotFoundException());
    mockMvc.perform(get(BASE_URL + "{postSlug}", slug))
           .andExpect(status().isNotFound())
           .andDo(document("get_post_not_found", SLUG_PATH_PARAMETER, ERROR_RESPONSE_FIELDS));
  }

  @Test
  void partiallyUpdatePost() throws Exception {
    var postDto = TestsUtil.sampleFullPost();
    mockMvc.perform(patch(BASE_URL + "{postSlug}", postDto.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isOk())
           .andDo(document("partial_update_post",
                           SLUG_PATH_PARAMETER,
                           REQUEST_FIELDS,
                           SLUG_INFO_RESPONSE_FIELDS));
  }

  @Test
  void fullyUpdatePost() throws Exception {
    var postDto = TestsUtil.sampleFullPost();
    mockMvc.perform(put(BASE_URL + "{postSlug}", postDto.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isOk())
           .andDo(document("full_update_post",
                           SLUG_PATH_PARAMETER,
                           REQUEST_FIELDS,
                           SLUG_INFO_RESPONSE_FIELDS));
  }

  @Test
  void deletePost() throws Exception {
    mockMvc.perform(delete(BASE_URL + "{postSlug}", TestsUtil.samplePostSlug().getSlug()))
           .andExpect(status().isNoContent())
           .andDo(document("delete_post", SLUG_PATH_PARAMETER));
  }
}
