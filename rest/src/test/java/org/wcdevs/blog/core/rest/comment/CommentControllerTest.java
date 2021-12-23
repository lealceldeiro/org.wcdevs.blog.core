package org.wcdevs.blog.core.rest.comment;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wcdevs.blog.core.rest.TestsUtil.ERROR_RESPONSE_FIELDS;
import static org.wcdevs.blog.core.rest.TestsUtil.MAPPER;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.wcdevs.blog.core.common.comment.CommentService;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;
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
  private static final String BASE_URL = "/comment/";

  private static final String POST_SLUG = "postSlug";
  private static final String POST_SLUG_DESC
      = "The slug of the post where the comment is being added";

  private static final String BODY = "body";
  private static final String BODY_DESC = "Content of the comment";

  private static final String PUBLISHED_BY = "publishedBy";
  private static final String PUBLISHED_BY_DESC = "Author of the comment";

  private static final String ANCHOR = "anchor";
  private static final String ANCHOR_DESC
      = "Comment anchor. This is generated during the creation of the comment and should be used"
        + "later to identify (and retrieve any information) about the comment later";

  private static final String PARENT_COMMENT_ANCHOR_PARAM = "parentAnchor";
  private static final String PARENT_COMMENT_ANCHOR_PARAM_DESC
      = "Parent comment anchor. This is the anchor of the comment under which the current comment "
        + "is nested";

  private static final String PARENT_COMMENT_ANCHOR = "parentCommentAnchor";
  private static final String PARENT_COMMENT_ANCHOR_DESC
      = "Anchor of the parent comment. This will be null for root comments (those not nested under"
        + " any other comment)";

  private static final String LAST_UPDATED = "lastUpdated";
  private static final String LAST_UPDATED_DESC = "Last time the comment was updated";

  private static final String CHILDREN_COUNT = "childrenCount";
  private static final String CHILDREN_COUNT_DESC
      = "Number of comments which are children of this comment. Those comments (children) can be "
        + "seen as the number of replies to the parent comment";

  private static final Object STRING_TYPE = JsonFieldType.STRING;
  private static final Object INTEGER_TYPE = JsonFieldType.NUMBER;
  private static final Object LOCAL_DATE_TIME_TYPE = JsonFieldType.STRING;

  private static final PathParametersSnippet ANCHOR_PATH_PARAMETER
      = pathParameters(parameterWithName("commentAnchor").description(ANCHOR_DESC));

  private static final PathParametersSnippet POST_SLUG_PATH_PARAMETER
      = pathParameters(parameterWithName(POST_SLUG).description(POST_SLUG_DESC));

  private static final PathParametersSnippet PARENT_COMMENT_ANCHOR_PATH_PARAM
      = pathParameters(parameterWithName(PARENT_COMMENT_ANCHOR_PARAM)
                           .description(PARENT_COMMENT_ANCHOR_PARAM_DESC));

  private static final FieldDescriptor[] fields = {
      fieldWithPath(POST_SLUG).description(POST_SLUG_DESC),
      fieldWithPath(BODY).description(BODY_DESC),
      fieldWithPath(PUBLISHED_BY).description(PUBLISHED_BY_DESC),
      fieldWithPath(PARENT_COMMENT_ANCHOR).optional().type(STRING_TYPE)
          .description(PARENT_COMMENT_ANCHOR_DESC),
      fieldWithPath(ANCHOR).optional().type(STRING_TYPE).description(ANCHOR_DESC),
      fieldWithPath(CHILDREN_COUNT).optional().type(INTEGER_TYPE).description(CHILDREN_COUNT_DESC),
      fieldWithPath(LAST_UPDATED).optional().type(LOCAL_DATE_TIME_TYPE)
          .description(LAST_UPDATED_DESC)
  };

  private static final FieldDescriptor[] arrFields
      = Stream.concat(Stream.of(fieldWithPath("[]").description("List of comments")),
                      Arrays.stream(fields).map(CommentControllerTest::toArrayFieldDescriptor))
              .toArray(FieldDescriptor[]::new);

  private static final RequestFieldsSnippet COMMENT_REQUEST_FIELDS = requestFields(fields);
  private static final ResponseFieldsSnippet COMMENT_RESPONSE_FIELDS = responseFields(fields);
  private static final ResponseFieldsSnippet COMMENTS_RESPONSE_FIELDS = responseFields(arrFields);

  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;

  @MockBean
  private CommentService commentService;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(documentationConfiguration(restDocumentationContextProvider))
                             .build();
  }

  private static FieldDescriptor toArrayFieldDescriptor(FieldDescriptor singleFieldDescriptor) {
    var path = singleFieldDescriptor.getPath();
    var description = singleFieldDescriptor.getDescription();
    var type = singleFieldDescriptor.getType();

    var newFd = fieldWithPath("[*]." + path).description(description).type(type);
    if (singleFieldDescriptor.isOptional()) {
      newFd.optional();
    }
    return singleFieldDescriptor.isIgnored() ? newFd.ignored() : newFd;
  }

  private static Stream<Arguments> createCommentArgs() {
    return Stream.of(arguments("create_root_comment", TestsUtil.sampleRootComment()),
                     arguments("create_child_comment", TestsUtil.sampleChildComment()));
  }

  @ParameterizedTest
  @MethodSource("createCommentArgs")
  void createComment(String documentSnippetId, CommentDto dto) throws Exception {
    when(commentService.createComment(dto)).thenReturn(dto);

    mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(dto)))
           .andExpect(status().isCreated())
           .andDo(document(documentSnippetId, COMMENT_REQUEST_FIELDS, COMMENT_RESPONSE_FIELDS));
  }

  @Test
  void getComment() throws Exception {
    var dto = TestsUtil.sampleComment();
    var anchor = dto.getAnchor();
    when(commentService.getComment(anchor)).thenReturn(dto);

    mockMvc.perform(get(BASE_URL + "{commentAnchor}", anchor))
           .andExpect(status().isOk())
           .andDo(document("get_comment", ANCHOR_PATH_PARAMETER, COMMENT_RESPONSE_FIELDS));
  }

  @Test
  void getAllPostComments() throws Exception {
    var comments = TestsUtil.sampleComments();
    var postSlug = comments.get(0).getPostSlug();
    when(commentService.getAllPostComments(postSlug)).thenReturn(comments);

    mockMvc.perform(get(BASE_URL + "all/{postSlug}", postSlug))
           .andExpect(status().isOk())
           .andDo(document("get_all_comments", POST_SLUG_PATH_PARAMETER, COMMENTS_RESPONSE_FIELDS));
  }

  @Test
  void getRootPostComments() throws Exception {
    var comments = TestsUtil.sampleRootComments();
    var postSlug = comments.get(0).getPostSlug();
    when(commentService.getRootPostComments(postSlug)).thenReturn(comments);

    mockMvc.perform(get(BASE_URL + "root/{postSlug}", postSlug))
           .andExpect(status().isOk())
           .andDo(document("get_root_comments", POST_SLUG_PATH_PARAMETER,
                           COMMENTS_RESPONSE_FIELDS));
  }

  @Test
  void getChildren() throws Exception {
    var comments = TestsUtil.sampleChildComments();
    var parentCommentAnchor = comments.get(0).getParentCommentAnchor();
    when(commentService.getCommentChildComments(parentCommentAnchor)).thenReturn(comments);

    mockMvc.perform(get(BASE_URL + "children/{parentAnchor}", parentCommentAnchor))
           .andExpect(status().isOk())
           .andDo(document("get_child_comments", PARENT_COMMENT_ANCHOR_PATH_PARAM,
                           COMMENTS_RESPONSE_FIELDS));
  }

  @Test
  void getChildrenMethodNotSupported() throws Exception {
    var sample = TestsUtil.sampleChildComments().get(0);

    mockMvc.perform(put(BASE_URL + "children/{parentAnchor}", sample.getParentCommentAnchor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(sample)))
           .andExpect(status().isMethodNotAllowed())
           .andDo(document("get_child_comments_method_not_allowed", PARENT_COMMENT_ANCHOR_PATH_PARAM,
                           ERROR_RESPONSE_FIELDS));
  }

  @Test
  void updateComment() throws Exception {
    var sample = TestsUtil.sampleComment();
    var anchor = sample.getAnchor();
    var dto = PartialCommentDto.builder().body(sample.getBody()).build();

    when(commentService.updateComment(anchor, dto)).thenReturn(sample);

    mockMvc.perform(put(BASE_URL + "{commentAnchor}", anchor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(dto)))
           .andExpect(status().isOk())
           .andDo(document("update_comment", ANCHOR_PATH_PARAMETER,
                           requestFields(fieldWithPath(BODY).description(BODY_DESC)),
                           COMMENT_RESPONSE_FIELDS));
  }

  @Test
  void deleteComment() throws Exception {
    var anchor = TestsUtil.sampleComment().getAnchor();

    mockMvc.perform(delete(BASE_URL + "{commentAnchor}", anchor))
           .andExpect(status().isNoContent())
           .andDo(document("get_comment", ANCHOR_PATH_PARAMETER));
    verify(commentService, times(1)).deleteComment(anchor);
  }
}
