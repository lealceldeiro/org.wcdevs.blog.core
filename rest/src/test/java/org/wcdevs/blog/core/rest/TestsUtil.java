package org.wcdevs.blog.core.rest;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.util.ResourceUtils;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.post.PostDto;

public final class TestsUtil {
  public static final ResponseFieldsSnippet ERROR_RESPONSE_FIELDS
      = responseFields(fieldWithPath("message").description("Error message"),
                       fieldWithPath("context").description("Request context"),
                       fieldWithPath("dateTime").description("Request date time"));

  private static final Random RANDOM = new SecureRandom();

  private static List<PostDto> SAMPLE_SLUG_DATA;
  private static List<PostDto> SAMPLE_SLUG_TITLE_DATA;
  private static List<PostDto> SAMPLE_TITLE_BODY_DATA;
  private static List<PostDto> SAMPLE_FULL_POST_DATA;
  private static List<CommentDto> SAMPLE_COMMENTS;

  public static final ObjectMapper MAPPER = JsonMapper.builder()
                                                      .addModule(new JavaTimeModule())
                                                      .build();

  static {
    try {
      SAMPLE_SLUG_DATA = readPosts("sample-post-slugs.json");
      SAMPLE_SLUG_TITLE_DATA = readPosts("sample-post-excerpts-slugs-and-titles.json");
      SAMPLE_TITLE_BODY_DATA = readPosts("sample-new-post-payload.json");
      SAMPLE_FULL_POST_DATA = readPosts("sample-full-posts.json");
      SAMPLE_COMMENTS = readComments("sample-comments.json");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<PostDto> readPosts(String file) throws IOException {
    var fileName = String.format("classpath:%s", file);

    return TestsUtil.MAPPER.readValue(ResourceUtils.getFile(fileName), new TypeReference<>() {
    });
  }

  private static List<CommentDto> readComments(String file) throws IOException {
    var fileName = String.format("classpath:%s", file);

    return TestsUtil.MAPPER.readValue(ResourceUtils.getFile(fileName), new TypeReference<>() {
    });
  }

  public static PostDto samplePostTitleBody() {
    return nextElementFrom(SAMPLE_TITLE_BODY_DATA);
  }

  private static <T> T nextElementFrom(List<T> collection) {
    return collection.get(RANDOM.nextInt(collection.size()));
  }

  public static PostDto samplePostSlug() {
    return nextElementFrom(SAMPLE_SLUG_DATA);
  }

  public static List<PostDto> samplePostSlugTitles() {
    return elements(SAMPLE_SLUG_TITLE_DATA);
  }

  private static <T> List<T> elements(List<T> elements) {
    return Collections.unmodifiableList(elements);
  }

  public static PostDto sampleFullPost() {
    return nextElementFrom(SAMPLE_FULL_POST_DATA);
  }

  public static String aString() {
    return UUID.randomUUID().toString();
  }

  public static CommentDto sampleComment() {
    return nextElementFrom(SAMPLE_COMMENTS);
  }

  public static List<CommentDto> sampleChildComments() {
    return commentsWhere(c -> Objects.nonNull(c.getParentCommentAnchor()));
  }

  public static CommentDto sampleChildComment() {
    return commentsWhere(c -> Objects.nonNull(c.getParentCommentAnchor())).get(0);
  }

  public static List<CommentDto> sampleRootComments() {
    return commentsWhere(c -> Objects.isNull(c.getParentCommentAnchor()));
  }

  public static CommentDto sampleRootComment() {
    return commentsWhere(c -> Objects.isNull(c.getParentCommentAnchor())).get(0);
  }

  private static List<CommentDto> commentsWhere(Predicate<CommentDto> predicate) {
    return elements(SAMPLE_COMMENTS).stream()
                                    .filter(predicate)
                                    .collect(Collectors.toList());
  }

  public static List<CommentDto> sampleComments() {
    return elements(SAMPLE_COMMENTS);
  }
}
