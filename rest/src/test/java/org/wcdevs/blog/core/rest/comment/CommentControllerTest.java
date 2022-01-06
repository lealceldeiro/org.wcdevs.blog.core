package org.wcdevs.blog.core.rest.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wcdevs.blog.core.rest.DocUtil.ANCHOR_DESC;
import static org.wcdevs.blog.core.rest.DocUtil.BASE_URL;
import static org.wcdevs.blog.core.rest.DocUtil.BODY;
import static org.wcdevs.blog.core.rest.DocUtil.BODY_DESC;
import static org.wcdevs.blog.core.rest.DocUtil.COMMENT_RESPONSE_FIELDS;
import static org.wcdevs.blog.core.rest.DocUtil.PARENT_COMMENT_ANCHOR_PARAM;
import static org.wcdevs.blog.core.rest.DocUtil.PARENT_COMMENT_ANCHOR_PARAM_DESC;
import static org.wcdevs.blog.core.rest.TestsUtil.ERROR_RESPONSE_FIELDS;
import static org.wcdevs.blog.core.rest.TestsUtil.MAPPER;
import static org.wcdevs.blog.core.rest.TestsUtil.aString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.wcdevs.blog.core.common.comment.CommentService;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;
import org.wcdevs.blog.core.rest.DocUtil;
import org.wcdevs.blog.core.rest.TestsUtil;
import org.wcdevs.blog.core.rest.auth.AuthAttributeExtractor;
import org.wcdevs.blog.core.rest.auth.Role;
import org.wcdevs.blog.core.rest.auth.SecurityContextAuthChecker;
import org.wcdevs.blog.core.rest.exceptionhandler.ControllerExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.ExceptionHandlerFactory;
import org.wcdevs.blog.core.rest.exceptionhandler.impl.ArgumentNotValidExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.impl.DataIntegrityViolationExceptionHandler;
import org.wcdevs.blog.core.rest.exceptionhandler.impl.NotFoundExceptionHandler;

@EnableWebMvc
@EnableSpringDataWebSupport
@SpringBootTest(classes = {
    CommentController.class, ControllerExceptionHandler.class, ExceptionHandlerFactory.class,
    NotFoundExceptionHandler.class, DataIntegrityViolationExceptionHandler.class,
    ArgumentNotValidExceptionHandler.class
})
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class CommentControllerTest {
  private static final PathParametersSnippet ANCHOR_PATH_PARAMETER
      = pathParameters(parameterWithName("commentAnchor").description(ANCHOR_DESC));

  private static final PathParametersSnippet PARENT_COMMENT_ANCHOR_PATH_PARAM
      = pathParameters(parameterWithName(PARENT_COMMENT_ANCHOR_PARAM)
                           .description(PARENT_COMMENT_ANCHOR_PARAM_DESC));

  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;

  @MockBean
  private CommentService commentService;
  @MockBean
  private AuthAttributeExtractor authAttributeExtractor;
  @MockBean
  private SecurityContextAuthChecker securityContextAuthChecker;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(documentationConfiguration(restDocumentationContextProvider))
                             .alwaysDo(print())
                             .build();
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
  void getChildren() throws Exception {
    var comments = TestsUtil.sampleChildComments();
    var parentCommentAnchor = comments.get(0).getParentCommentAnchor();
    when(commentService.getParentCommentChildren(eq(parentCommentAnchor), any(Pageable.class)))
        .thenReturn(TestsUtil.pageOf(comments));

    var responseFields = DocUtil.pageableFieldsWith(DocUtil.COMMENT_ARR_fIELDS);

    mockMvc.perform(get(BASE_URL + "children/{parentAnchor}", parentCommentAnchor))
           .andExpect(status().isOk())
           .andDo(document("get_child_comments", PARENT_COMMENT_ANCHOR_PATH_PARAM, responseFields));
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
    var username = aString();
    var dto = PartialCommentDto.builder().body(sample.getBody()).build();

    when(authAttributeExtractor.principalUsername(any())).thenReturn(username);
    when(commentService.updateComment(anchor, dto, username)).thenReturn(sample);

    mockMvc.perform(put(BASE_URL + "{commentAnchor}", anchor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(dto)))
           .andExpect(status().isOk())
           .andDo(document("update_comment", ANCHOR_PATH_PARAMETER,
                           requestFields(fieldWithPath(BODY).description(BODY_DESC)),
                           COMMENT_RESPONSE_FIELDS));
  }

  @Test
  void deleteCommentByAuthor() throws Exception {
    var anchor = TestsUtil.sampleComment().getAnchor();
    var user = TestsUtil.sampleComment().getAnchor();

    when(securityContextAuthChecker.hasAnyRole(Role.EDITOR)).thenReturn(false);
    when(authAttributeExtractor.principalUsername(any())).thenReturn(user);

    mockMvc.perform(delete(BASE_URL + "{commentAnchor}", anchor))
           .andExpect(status().isNoContent())
           .andDo(document("delete_comment", ANCHOR_PATH_PARAMETER));
    verify(commentService, times(1)).deleteComment(anchor, user);
    verify(commentService, never()).deleteComment(anchor);
  }

  @Test
  void deleteCommentByEditor() throws Exception {
    var anchor = TestsUtil.sampleComment().getAnchor();

    when(securityContextAuthChecker.hasAnyRole(Role.EDITOR)).thenReturn(true);

    mockMvc.perform(delete(BASE_URL + "{commentAnchor}", anchor))
           .andExpect(status().isNoContent());
    verify(commentService, times(1)).deleteComment(anchor);
    verify(commentService, never()).deleteComment(eq(anchor), any());
  }
}
