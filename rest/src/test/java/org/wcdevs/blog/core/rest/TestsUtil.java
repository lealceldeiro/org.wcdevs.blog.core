package org.wcdevs.blog.core.rest;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.util.ResourceUtils;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.post.PostDto;
import org.wcdevs.blog.core.persistence.post.PostStatus;

public final class TestsUtil {
  public static final ResponseFieldsSnippet ERROR_RESPONSE_FIELDS
      = responseFields(fieldWithPath("message").description("Error message"),
                       fieldWithPath("context").description("Request context"),
                       fieldWithPath("dateTime").description("Request date time"));

  private static final Random RANDOM = new SecureRandom();

  private static List<PostDto> SAMPLE_SLUG_DATA;
  private static List<PostDto> SAMPLE_POST_LITE_DATA;
  private static List<PostDto> SAMPLE_TITLE_BODY_DATA;
  private static List<PostDto> SAMPLE_FULL_POST_DATA;
  private static List<CommentDto> SAMPLE_COMMENTS;

  public static final ObjectMapper MAPPER = JsonMapper.builder()
                                                      .addModule(new JavaTimeModule())
                                                      .defaultLocale(Locale.ENGLISH)
                                                      .defaultTimeZone(TimeZone.getTimeZone("UTC"))
                                                      .build();

  private static final String[] RANDOM_USERS = {"john", "susan", "edi", "sam", "peter", "jules"};

  static {
    try {
      SAMPLE_SLUG_DATA = readPosts("sample-post-slugs.json");
      SAMPLE_POST_LITE_DATA = readPosts("sample-post-lite-data.json");
      SAMPLE_TITLE_BODY_DATA = readPosts("sample-new-post-payload.json");
      SAMPLE_FULL_POST_DATA = readPosts("sample-full-posts.json");
      SAMPLE_COMMENTS = readComments("sample-comments.json");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String randomUsername() {
    return RANDOM_USERS[RANDOM.nextInt(RANDOM_USERS.length)];
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
    var dto = nextElementFrom(SAMPLE_SLUG_DATA);
    dto.setStatus(null);
    return dto;
  }

  public static List<PostDto> samplePostsLiteData() {
    return samplePostsLiteData(null);
  }

  public static List<PostDto> samplePostsLiteData(PostStatus status) {
    return elements(SAMPLE_POST_LITE_DATA)
        .stream()
        .filter(p -> Objects.isNull(status) || p.getStatus() == status)
        .collect(Collectors.toList());
  }

  private static <T> List<T> elements(List<T> elements) {
    return Collections.unmodifiableList(elements);
  }

  public static PostDto sampleFullPost() {
    return nextElementFrom(SAMPLE_FULL_POST_DATA);
  }

  public static PostDto.PostDtoBuilder sampleFullPostBuilder() {
    return builderFrom(nextElementFrom(SAMPLE_FULL_POST_DATA));
  }

  public static PostDto.PostDtoBuilder builderFrom(PostDto proto) {
    return PostDto.builder()
                  .title(proto.getTitle())
                  .slug(proto.getSlug())
                  .body(proto.getBody())
                  .excerpt(proto.getExcerpt())
                  .publishedBy(proto.getPublishedBy())
                  .updatedBy(proto.getUpdatedBy())
                  .publishedOn(proto.getPublishedOn())
                  .updatedOn(proto.getUpdatedOn())
                  .status(null);
  }

  public static CommentDto.CommentDtoBuilder builderFrom(CommentDto proto) {
    return CommentDto.builder()
                     .anchor(proto.getAnchor())
                     .body(proto.getBody())
                     .parentCommentAnchor(proto.getParentCommentAnchor())
                     .publishedBy(proto.getPublishedBy())
                     .lastUpdated(proto.getLastUpdated())
                     .post(proto.getPost())
                     .childrenCount(proto.getChildrenCount());
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

  public static <T> Page<T> pageOf(List<T> content, Pageable pageable) {
    return new PageImpl<>(content, pageable, content.size());
  }

  public static <T> Page<T> pageOf(List<T> content) {
    return pageOf(content, Pageable.ofSize(10));
  }

  @SafeVarargs
  public static <T> Page<T> pageOf(T element, T... elements) {
    return pageOf(Stream.concat(Arrays.stream(elements),
                                Stream.of(element))
                        .collect(Collectors.toList()));
  }

  public static Pageable pageable() {
    return Pageable.ofSize(10);
  }

  public static PostStatus aRandomPostStatus() {
    var values = PostStatus.values();
    return values[RANDOM.nextInt(values.length)];
  }
}
