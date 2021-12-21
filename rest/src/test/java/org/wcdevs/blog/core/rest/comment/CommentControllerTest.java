package org.wcdevs.blog.core.rest.comment;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wcdevs.blog.core.rest.TestsUtil.MAPPER;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.wcdevs.blog.core.common.comment.CommentService;
import org.wcdevs.blog.core.rest.AppExceptionHandler;
import org.wcdevs.blog.core.rest.TestsUtil;
import org.wcdevs.blog.core.rest.errorhandler.ErrorHandlerFactory;
import org.wcdevs.blog.core.rest.errorhandler.impl.ArgumentNotValidExceptionHandler;
import org.wcdevs.blog.core.rest.errorhandler.impl.DataIntegrityViolationErrorHandler;
import org.wcdevs.blog.core.rest.errorhandler.impl.NotFoundErrorHandler;

@EnableWebMvc
@SpringBootTest(classes = {
    CommentController.class, AppExceptionHandler.class, ErrorHandlerFactory.class,
    NotFoundErrorHandler.class, DataIntegrityViolationErrorHandler.class,
    ArgumentNotValidExceptionHandler.class
})
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class CommentControllerTest {
  private static final String BASE_URL = "/comment";

  private static final RequestFieldsSnippet ROOT_COMMENT_REQUEST_FIELDS
      = requestFields(fieldWithPath("postSlug")
                          .description("The slug of the post where the comment is being added"),
                      fieldWithPath("body").description("Content of the comment"),
                      fieldWithPath("publishedBy").description("Author of the comment"),
                      fieldWithPath("anchor").ignored(),
                      fieldWithPath("lastUpdated").ignored());
  private static final ResponseFieldsSnippet COMMENT_RESPONSE_FIELDS
      = responseFields(fieldWithPath("anchor")
                           .description("Comment anchor. This value must be used to identify (and "
                                        + "retrieve) the comment later"),
                       fieldWithPath("postSlug").ignored(),
                       fieldWithPath("body").ignored(),
                       fieldWithPath("publishedBy").ignored(),
                       fieldWithPath("lastUpdated").ignored());

  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;

  @MockBean
  private CommentService commentService;

  @BeforeEach
  void setUpd(RestDocumentationContextProvider restDocumentationContextProvider) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(documentationConfiguration(restDocumentationContextProvider))
                             .build();
    when(commentService.getComment(anyString())).thenReturn(TestsUtil.sampleComment());
    when(commentService.getCommentChildComments(anyString()))
        .thenReturn(TestsUtil.sampleChildComments());
    when(commentService.getAllPostComments(anyString())).thenReturn(TestsUtil.sampleComments());
    when(commentService.getRootPostComments(anyString())).thenReturn(TestsUtil.sampleRootComments());
  }

  @Test
  void createRootComment() throws Exception {
    var commentDto = TestsUtil.sampleRootComment();
    when(commentService.createComment(commentDto)).thenReturn(commentDto);

    mockMvc.perform(post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(commentDto)))
           .andExpect(status().isCreated())
           .andDo(document("create_root_comment",
                           ROOT_COMMENT_REQUEST_FIELDS, COMMENT_RESPONSE_FIELDS));
  }

  @Test
  void getComment() {
  }

  @Test
  void getAllPostComments() {
  }

  @Test
  void getRootPostComments() {
  }

  @Test
  void getChildren() {
  }

  @Test
  void updateComment() {
  }

  @Test
  void deleteComment() {
  }
}
