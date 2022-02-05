package org.wcdevs.blog.core.rest.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.wcdevs.blog.core.common.comment.CommentService;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;
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
@ExtendWith({SpringExtension.class})
class CommentControllerTest {
  private static final String COMMENT_URL = "/comment/";

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
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(print()).build();
  }

  @Test
  void getComment() throws Exception {
    var dto = TestsUtil.sampleComment();
    var anchor = dto.getAnchor();
    when(commentService.getComment(anchor)).thenReturn(dto);

    mockMvc.perform(get(COMMENT_URL + "{commentAnchor}", anchor))
           .andExpect(status().isOk());
  }

  @Test
  void getChildren() throws Exception {
    var comments = TestsUtil.sampleChildComments();
    var parentCommentAnchor = comments.get(0).getParentCommentAnchor();
    when(commentService.getParentCommentChildren(eq(parentCommentAnchor), any(Pageable.class)))
        .thenReturn(TestsUtil.pageOf(comments));

    mockMvc.perform(get(COMMENT_URL + "children/{parentAnchor}", parentCommentAnchor))
           .andExpect(status().isOk());
  }

  @Test
  void getChildrenMethodNotSupported() throws Exception {
    var sample = TestsUtil.builderFrom(TestsUtil.sampleChildComment())
                          .publishedBy(null)
                          .lastUpdated(null)
                          .build();

    mockMvc.perform(put(COMMENT_URL + "children/{parentAnchor}", sample.getParentCommentAnchor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(sample)))
           .andExpect(status().isMethodNotAllowed());
  }

  @Test
  void updateComment() throws Exception {
    var sample = TestsUtil.sampleComment();
    var anchor = sample.getAnchor();
    var username = aString();
    var dto = PartialCommentDto.builder().body(sample.getBody()).build();

    when(authAttributeExtractor.principalUsername(any())).thenReturn(username);
    when(commentService.updateComment(anchor, dto, username))
        .thenReturn(CommentDto.builder().anchor(anchor).build());

    mockMvc.perform(put(COMMENT_URL + "{commentAnchor}", anchor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(dto)))
           .andExpect(status().isOk());
  }

  @Test
  void deleteCommentByAuthor() throws Exception {
    var anchor = TestsUtil.sampleComment().getAnchor();
    var user = TestsUtil.sampleComment().getAnchor();

    when(securityContextAuthChecker.hasAnyRole(Role.EDITOR)).thenReturn(false);
    when(authAttributeExtractor.principalUsername(any())).thenReturn(user);

    mockMvc.perform(delete(COMMENT_URL + "{commentAnchor}", anchor))
           .andExpect(status().isNoContent());

    verify(commentService, times(1)).deleteComment(anchor, user);
    verify(commentService, never()).deleteComment(anchor);
  }

  @Test
  void deleteCommentByEditor() throws Exception {
    var anchor = TestsUtil.sampleComment().getAnchor();

    when(securityContextAuthChecker.hasAnyRole(Role.EDITOR)).thenReturn(true);

    mockMvc.perform(delete(COMMENT_URL + "{commentAnchor}", anchor))
           .andExpect(status().isNoContent());

    verify(commentService, times(1)).deleteComment(anchor);
    verify(commentService, never()).deleteComment(eq(anchor), any());
  }
}
