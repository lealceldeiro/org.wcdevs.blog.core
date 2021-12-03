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
import static org.wcdevs.blog.core.rest.TestsUtil.MAPPER;
import static org.wcdevs.blog.core.rest.TestsUtil.nextPostSlugSample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
  private static final String BASE_URL = "/post";
  private static final PathParametersSnippet SLUG_PATH_PARAMETER
      = pathParameters(parameterWithName("postSlug")
                           .description("Post slug generated during creation"));
  private static final RequestFieldsSnippet TITLE_AND_BODY_REQUEST_FIELDS
      = requestFields(fieldWithPath("title").description("Post title"),
                      fieldWithPath("body").description("Body of the post."),
                      fieldWithPath("slug").ignored(),
                      fieldWithPath("publishedOn").ignored(),
                      fieldWithPath("updatedOn").ignored());
  private static final ResponseFieldsSnippet SLUG_INFO_RESPONSE_FIELDS
      = responseFields(
      fieldWithPath("slug")
          .description("Post slug. This value can be used to retrieve the post later"));
  private static final ResponseFieldsSnippet ERROR_RESPONSE_FIELDS
      = responseFields(fieldWithPath("message").description("Error message"),
                       fieldWithPath("context").description("Request context"),
                       fieldWithPath("dateTime").description("Request date time"));

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
        then(ignored -> TestsUtil.nextPostSlugSample());
    when(postService.getPost(anyString())).then(ignored -> TestsUtil.nextFullPostSample());
    when(postService.partialUpdate(anyString(), any(PartialPostDto.class)))
        .then(ignored -> TestsUtil.nextPostSlugSample());
    when(postService.fullUpdate(anyString(), any(PostDto.class)))
        .then(ignored -> TestsUtil.nextPostSlugSample());
    when(postService.getPosts()).then(ignored -> TestsUtil.postSlugTitleSamples());
  }

  @Test
  void getPosts() throws Exception {
    mockMvc.perform(get(BASE_URL + "/"))
           .andExpect(status().isOk())
           .andDo(document("get_posts",
                           responseFields(fieldWithPath("[]")
                                              .description("List of posts information"),
                                          fieldWithPath("[*].title")
                                              .description("Post title"),
                                          fieldWithPath("[*].slug")
                                              .description("Post slug. This value can be used to "
                                                           + "retrieve the post later")
                                         )
                          )
                 );
  }

  @Test
  void createPost() throws Exception {
    var postDto = TestsUtil.nextPostTitleBodySample();
    mockMvc.perform(post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isCreated())
           .andDo(document("create_post",
                           requestFields(fieldWithPath("title").description("Post title"),
                                         fieldWithPath("body").description("Body of the post")),
                           SLUG_INFO_RESPONSE_FIELDS
                          )
                 );
  }

  @Test
  void createPostDBError() throws Exception {
    var postDto = TestsUtil.nextPostTitleBodySample();
    var err = String.format("There's already a post with title %s", postDto.getSlug());
    when(postService.createPost(postDto)).thenThrow(new DataIntegrityViolationException(err));
    mockMvc.perform(post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isConflict())
           .andDo(document("create_post_db_error",
                           requestFields(fieldWithPath("title")
                                             .description("Post title. It must be unique"),
                                         fieldWithPath("body").description("Body of the post")),
                           ERROR_RESPONSE_FIELDS
                          )
                 );
  }

  @Test
  void createPostDBErrorWithRootCause() throws Exception {
    var postDto = TestsUtil.nextPostTitleBodySample();

    var cause = mock(Throwable.class);
    when(cause.getMessage()).thenReturn("PK constraint violation");
    var errMessage = String.format("There's already a post with title %s", postDto.getSlug());

    var ex = mock(DataIntegrityViolationException.class);
    when(ex.getMessage()).thenReturn(errMessage);
    when(ex.getRootCause()).thenReturn(cause);

    when(postService.createPost(postDto)).thenThrow(ex);

    mockMvc.perform(post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isConflict());
  }

  @Test
  void getPost() throws Exception {
    var postDto = TestsUtil.nextPostSlugSample();
    mockMvc.perform(get(BASE_URL + "/{postSlug}", postDto.getSlug()))
           .andExpect(status().isOk())
           .andDo(document("get_post",
                           SLUG_PATH_PARAMETER,
                           responseFields(fieldWithPath("title").description("Post title"),
                                          fieldWithPath("slug")
                                              .description("Post slug. This value can be used to "
                                                           + "retrieve the post later"),
                                          fieldWithPath("body").description("Post body"),
                                          fieldWithPath("publishedOn")
                                              .description("Date time where the post was "
                                                           + "published"),
                                          fieldWithPath("updatedOn")
                                              .description("Date time where the post was last"
                                                           + " updated")
                                         )
                          )
                 );
  }

  @Test
  void getPostNotFound() throws Exception {
    var slug = nextPostSlugSample().getSlug();
    when(postService.getPost(slug)).thenThrow(new PostNotFoundException());
    mockMvc.perform(get(BASE_URL + "/{postSlug}", slug))
           .andExpect(status().isNotFound())
           .andDo(document("get_post_not_found", SLUG_PATH_PARAMETER, ERROR_RESPONSE_FIELDS));
  }

  @Test
  void partiallyUpdatePost() throws Exception {
    var postDto = TestsUtil.nextFullPostSample();
    mockMvc.perform(patch(BASE_URL + "/{postSlug}", postDto.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isOk())
           .andDo(document("partial_update_post",
                           SLUG_PATH_PARAMETER,
                           TITLE_AND_BODY_REQUEST_FIELDS,
                           SLUG_INFO_RESPONSE_FIELDS));
  }

  @Test
  void fullyUpdatePost() throws Exception {
    var postDto = TestsUtil.nextFullPostSample();
    mockMvc.perform(put(BASE_URL + "/{postSlug}", postDto.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isOk())
           .andDo(document("full_update_post",
                           SLUG_PATH_PARAMETER,
                           TITLE_AND_BODY_REQUEST_FIELDS,
                           SLUG_INFO_RESPONSE_FIELDS));
  }

  @Test
  void deletePost() throws Exception {
    mockMvc.perform(delete(BASE_URL + "/{postSlug}", TestsUtil.nextPostSlugSample().getSlug()))
           .andExpect(status().isNoContent())
           .andDo(document("delete_post", SLUG_PATH_PARAMETER));
  }
}
