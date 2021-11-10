package org.wcdevs.blog.core.rest.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.springframework.util.ResourceUtils;
import org.wcdevs.blog.core.persistence.post.PostDto;

public final class TestsUtil {
  private static final Random RANDOM = new SecureRandom();
  private static List<PostDto> SAMPLE_SLUG_DATA;
  private static List<PostDto> SAMPLE_SLUG_TITLE_DATA;
  private static List<PostDto> SAMPLE_TITLE_BODY_DATA;
  private static List<PostDto> SAMPLE_FULL_POST_DATA;

  public static final ObjectMapper MAPPER = JsonMapper.builder()
                                                      .addModule(new JavaTimeModule())
                                                      .build();

  static {
    try {
      SAMPLE_SLUG_DATA = read("sample-post-slugs.json");
      SAMPLE_SLUG_TITLE_DATA = read("sample-post-slugs-and-titles.json");
      SAMPLE_TITLE_BODY_DATA = read("sample-post-title-and-body.json");
      SAMPLE_FULL_POST_DATA = read("sample-full-posts.json");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static List<PostDto> read(String file) throws IOException {
    var fileName = String.format("classpath:%s", file);

    return TestsUtil.MAPPER.readValue(ResourceUtils.getFile(fileName), new TypeReference<>() {
    });
  }

  static PostDto nextPostTitleBodySample() {
    return SAMPLE_TITLE_BODY_DATA.get(RANDOM.nextInt(SAMPLE_TITLE_BODY_DATA.size()));
  }

  static PostDto nextPostSlugSample() {
    return SAMPLE_SLUG_DATA.get(RANDOM.nextInt(SAMPLE_SLUG_DATA.size()));
  }

  static PostDto nextPostSlugTitleSample() {
    return SAMPLE_SLUG_TITLE_DATA.get(RANDOM.nextInt(SAMPLE_SLUG_TITLE_DATA.size()));
  }

  static List<PostDto> postSlugTitleSamples() {
    return Collections.unmodifiableList(SAMPLE_SLUG_TITLE_DATA);
  }

  static PostDto nextFullPostSample() {
    return SAMPLE_FULL_POST_DATA.get(RANDOM.nextInt(SAMPLE_FULL_POST_DATA.size()));
  }

  static List<PostDto> fullPostSamples() {
    return Collections.unmodifiableList(SAMPLE_FULL_POST_DATA);
  }
}
