package org.wcdevs.blog.core.rest.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.wcdevs.blog.core.common.post.PostService;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.PostDto;
import static org.wcdevs.blog.core.rest.post.TestsUtil.MAPPER;

@EnableWebMvc
@SpringBootTest(classes = PostController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class PostControllerTest {
  private static final String BASE_URL = "/post";

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
    when(postService.updatePost(anyString(), any(PartialPostDto.class)))
        .then(ignored -> TestsUtil.nextPostSlugSample());
    when(postService.getPosts()).then(ignored -> TestsUtil.postSlugTitleSamples());
  }

  @Test
  void getPosts() throws Exception {
    mockMvc.perform(get(BASE_URL + "/"))
           .andExpect(status().isOk())
           .andDo(document("get_posts"));
  }

  @Test
  void createPost() throws Exception {
    var postDto = TestsUtil.nextPostTitleBodySample();
    mockMvc.perform(post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isCreated())
           .andDo(document("create_post"));
  }

  @Test
  void getPost() throws Exception {
    var postDto = TestsUtil.nextPostSlugSample();
    mockMvc.perform(get(BASE_URL + "/{postSlug}", postDto.getSlug()))
           .andExpect(status().isOk())
           .andDo(document("get_post"));
  }

  @Test
  void updatePost() throws Exception {
    var postDto = TestsUtil.nextFullPostSample();
    mockMvc.perform(put(BASE_URL + "/{postSlug}", postDto.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(postDto)))
           .andExpect(status().isOk())
           .andDo(document("update_post"));
  }

  @Test
  void deletePost() throws Exception {
    mockMvc.perform(delete(BASE_URL + "/{postSlug}", TestsUtil.nextPostSlugSample().getSlug()))
           .andExpect(status().isNoContent())
           .andDo(document("update_post"));
  }
}
