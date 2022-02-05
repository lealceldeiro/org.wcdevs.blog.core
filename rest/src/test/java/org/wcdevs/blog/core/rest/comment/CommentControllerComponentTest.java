package org.wcdevs.blog.core.rest.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
import static org.wcdevs.blog.core.rest.TestMock.POST_EXCERPT1;
import static org.wcdevs.blog.core.rest.TestMock.POST_EXCERPT2;
import static org.wcdevs.blog.core.rest.TestMock.POST_TITLE1;
import static org.wcdevs.blog.core.rest.TestMock.POST_TITLE2;
import static org.wcdevs.blog.core.rest.TestMock.anchor2;
import static org.wcdevs.blog.core.rest.TestMock.slug1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.wcdevs.blog.core.persistence.comment.Comment;
import org.wcdevs.blog.core.persistence.comment.CommentRepository;
import org.wcdevs.blog.core.persistence.post.Post;
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
import org.wcdevs.blog.core.rest.exceptionhandler.impl.NotFoundExceptionHandler;

@SpringBootTest(classes = {
    Application.class, CommentController.class, ControllerExceptionHandler.class,
    ExceptionHandlerFactory.class, NotFoundExceptionHandler.class,
    DataIntegrityViolationExceptionHandler.class, ArgumentNotValidExceptionHandler.class
})
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class CommentControllerComponentTest {
  private static final String COMMENT_URL = "/comment/";

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

  private String commentAuthor;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(documentationConfiguration(restDocumentationContextProvider))
                             .alwaysDo(print())
                             .build();
    commentAuthor = TestsUtil.randomUsername();
    when(authAttributeExtractor.principalUsername(any())).thenReturn(commentAuthor);
  }

  @AfterEach
  void tearDown() {
    // Clean state after the test completed, but no @Transaction:
    // https://www.javacodegeeks.com/2011/12/spring-pitfalls-transactional-tests.html
    // see also https://stackoverflow.com/a/37414387/5640649 for a different approach
    postRepository.deleteAll(); // will delete also all comments
  }

  @Test
  void getComment() throws Exception {
    // given
    var parentPost = givenPostWith(POST_TITLE1, slug1(), POST_BODY1, POST_EXCERPT1);

    var anchor = TestMock.anchor1();
    givenCommentWith(parentPost, anchor, COMMENT_BODY1);

    // when
    var resultActions = mockMvc.perform(get(COMMENT_URL + "{commentAnchor}", anchor));

    // then
    resultActions.andExpect(status().isOk())
                 .andDo(document("get_comment",
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("commentAnchor")
                                         .description("Comment anchor")
                                               ),
                                 responseFields(
                                     fieldWithPath("anchor")
                                         .description("Comment anchor: generated during the "
                                                      + "creation of the comment. It should be used"
                                                      + " to identify and retrieve any information"
                                                      + " about the comment"),
                                     fieldWithPath("body")
                                         .description("Content of the comment"),
                                     fieldWithPath("publishedBy")
                                         .description("Author of the comment"),
                                     fieldWithPath("lastUpdated")
                                         .description("Last time the comment was updated"),
                                     fieldWithPath("childrenCount")
                                         .description("Number of comments which are children of "
                                                      + "this comment. Those comments (children) "
                                                      + "can be seen as the number of replies to "
                                                      + "the parent comment")
                                               )
                                )
                       );
  }

  private Post givenPostWith(String title, String slug, String body, String excerpt) {
    var post = Post.builder()
                   .title(title)
                   .slug(slug)
                   .body(body)
                   .excerpt(excerpt)
                   .publishedOn(ClockUtil.utcNow())
                   .updatedOn(ClockUtil.utcNow())
                   .updatedBy(TestsUtil.randomUsername())
                   .publishedBy(TestsUtil.randomUsername())
                   .build();
    post.setStatus(PostStatus.PUBLISHED);
    return postRepository.save(post);
  }

  private Comment givenCommentWith(Post post, String anchor, String body) {
    return givenCommentWith(post, anchor, body, commentAuthor);
  }

  private Comment givenCommentWith(Post post, String anchor, String body, String commentAuthor) {
    return givenCommentWith(post, null, anchor, body, commentAuthor);
  }

  private Comment givenCommentWith(Post post, Comment parentComment, String anchor, String body) {
    return givenCommentWith(post, parentComment, anchor, body, commentAuthor);
  }

  private Comment givenCommentWith(Post post, Comment parentComment, String anchor, String body,
                                   String commentAuthor) {
    var comment = new Comment();
    comment.setAnchor(anchor);
    comment.setParentComment(parentComment);
    comment.setBody(body);
    comment.setLastUpdated(ClockUtil.utcNow());
    comment.setPublishedBy(commentAuthor);
    comment.setPost(post);
    return commentRepository.save(comment);
  }

  @Test
  void getChildren() throws Exception {
    // given
    var parentPost = givenPostWith(POST_TITLE2, slug1(), POST_BODY2, POST_EXCERPT2);

    var parentCommentAnchor = TestMock.anchor1();
    var parentComment = givenCommentWith(parentPost, parentCommentAnchor, COMMENT_BODY1);
    givenCommentWith(parentPost, parentComment, anchor2(), COMMENT_BODY2);

    // when
    var resultActions = mockMvc.perform(get(COMMENT_URL + "children/{parentAnchor}",
                                            parentCommentAnchor));
    // then
    resultActions.andExpect(status().isOk())
                 .andDo(document("get_child_comments",
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("parentAnchor")
                                         .description("Parent comment anchor: anchor of the comment"
                                                      + " under which the child comments are "
                                                      + "nested")
                                               ),
                                 DocUtil.pageableFieldsWith(
                                     fieldWithPath("content.[]").description("List of comments"),
                                     fieldWithPath("content.[*].anchor")
                                         .description("Comment anchor: generated during the "
                                                      + "creation of the comment. It should be used"
                                                      + " to identify and retrieve any information"
                                                      + " about the comment"),
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
  void getChildCommentsPutMethodNotSupported() throws Exception {
    // given
    var parentPost = givenPostWith(POST_TITLE1, slug1(), POST_BODY1, POST_EXCERPT1);

    var anchor = TestMock.anchor1();
    givenCommentWith(parentPost, anchor, COMMENT_BODY1);

    // language=JSON
    var payload = "{\n"
                  + "  \"body\": \"" + COMMENT_BODY2 + "\"\n"
                  + "}";
    // when
    var resultActions = mockMvc.perform(put(COMMENT_URL + "children/{parentAnchor}",
                                            anchor)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isMethodNotAllowed())
                 .andDo(document("get_child_comments_method_not_allowed",
                                 preprocessRequest(prettyPrint()),
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("parentAnchor")
                                         .description("Parent comment anchor: anchor of the comment"
                                                      + " under which the current comment is "
                                                      + "nested")
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
  void updateComment() throws Exception {
    // given
    var parentPost = givenPostWith(POST_TITLE2, slug1(), POST_BODY2, POST_EXCERPT2);

    var anchor = TestMock.anchor1();
    givenCommentWith(parentPost, anchor, COMMENT_BODY1);

    // language=JSON
    var payload = "{\n"
                  + "  \"body\": \"" + COMMENT_BODY2 + "\"\n"
                  + "}";
    // when
    var resultActions = mockMvc.perform(put(COMMENT_URL + "{commentAnchor}", anchor)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(payload));
    // then
    resultActions.andExpect(status().isOk())
                 .andDo(document("update_comment",
                                 preprocessRequest(prettyPrint()),
                                 preprocessResponse(prettyPrint()),
                                 pathParameters(
                                     parameterWithName("commentAnchor")
                                         .description("Comment anchor")
                                               ),
                                 requestFields(
                                     fieldWithPath("body")
                                         .description("Content of the comment")
                                              ),
                                 responseFields(
                                     fieldWithPath("anchor")
                                         .description("Comment anchor. This is generated during "
                                                      + "the creation of the comment and should be "
                                                      + "used later to identify and retrieve any "
                                                      + "information about the comment")
                                               )
                                )
                       );
  }

  @Test
  void deleteCommentByAuthor() throws Exception {
    // given
    var parentPost = givenPostWith(POST_TITLE1, slug1(), POST_BODY1, POST_EXCERPT1);

    var anchor = TestMock.anchor1();
    givenCommentWith(parentPost, anchor, COMMENT_BODY1);

    when(securityContextAuthChecker.hasAnyRole(Role.EDITOR)).thenReturn(false);
    when(authAttributeExtractor.principalUsername(any())).thenReturn(commentAuthor);

    // when
    var resultActions = mockMvc.perform(delete(COMMENT_URL + "{commentAnchor}", anchor));

    // then
    resultActions.andExpect(status().isNoContent())
                 .andDo(document("delete_comment",
                                 pathParameters(
                                     parameterWithName("commentAnchor")
                                         .description("Comment anchor")
                                               )
                                )
                       );
  }

  @Test
  void deleteCommentByEditor() throws Exception {
    // given
    var parentPost = givenPostWith(POST_TITLE1, slug1(), POST_BODY1, POST_EXCERPT1);

    var anchor = TestMock.anchor1();
    givenCommentWith(parentPost, anchor, COMMENT_BODY1);

    when(securityContextAuthChecker.hasAnyRole(Role.EDITOR)).thenReturn(true);
    when(authAttributeExtractor.principalUsername(any())).thenReturn(null);

    // when
    var resultActions = mockMvc.perform(delete(COMMENT_URL + "{commentAnchor}", anchor));

    // then
    resultActions.andExpect(status().isNoContent());
  }
}
