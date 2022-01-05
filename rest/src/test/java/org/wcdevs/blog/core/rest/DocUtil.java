package org.wcdevs.blog.core.rest;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.util.Arrays;
import java.util.stream.Stream;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public final class DocUtil {
  public static final String BASE_URL = "/comment/";

  public static final String POST_SLUG = "postSlug";
  public static final String POST_SLUG_DESC
      = "The slug of the post where the comment is published";

  public static final String BODY = "body";
  public static final String BODY_DESC = "Content of the comment";

  public static final String PUBLISHED_BY = "publishedBy";
  public static final String PUBLISHED_BY_DESC = "Author of the comment";

  public static final String ANCHOR = "anchor";
  public static final String ANCHOR_DESC
      = "Comment anchor. This is generated during the creation of the comment and should be used"
        + " later to identify and retrieve any information about the comment";

  public static final String PARENT_COMMENT_ANCHOR_PARAM = "parentAnchor";
  public static final String PARENT_COMMENT_ANCHOR_PARAM_DESC
      = "Parent comment anchor. This is the anchor of the comment under which the current comment "
        + "is nested";

  public static final String PARENT_COMMENT_ANCHOR = "parentCommentAnchor";
  public static final String PARENT_COMMENT_ANCHOR_DESC
      = "Anchor of the parent comment. This will be null for root comments (those not nested under"
        + " any other comment)";

  public static final String LAST_UPDATED = "lastUpdated";
  public static final String LAST_UPDATED_DESC = "Last time the comment was updated";

  public static final String CHILDREN_COUNT = "childrenCount";
  public static final String CHILDREN_COUNT_DESC
      = "Number of comments which are children of this comment. Those comments (children) can be "
        + "seen as the number of replies to the parent comment";

  public static final Object STRING_TYPE = JsonFieldType.STRING;
  public static final Object INTEGER_TYPE = JsonFieldType.NUMBER;
  public static final Object LOCAL_DATE_TIME_TYPE = JsonFieldType.STRING;

  public static final FieldDescriptor[] COMMENTS_FIELDS = {
      fieldWithPath(ANCHOR).optional().type(STRING_TYPE).description(ANCHOR_DESC),
      fieldWithPath(BODY).description(BODY_DESC),
      fieldWithPath(PUBLISHED_BY).description(PUBLISHED_BY_DESC),
      fieldWithPath(LAST_UPDATED).optional().type(LOCAL_DATE_TIME_TYPE)
          .description(LAST_UPDATED_DESC),
      fieldWithPath(CHILDREN_COUNT).optional().type(INTEGER_TYPE).description(CHILDREN_COUNT_DESC),
      fieldWithPath(POST_SLUG).optional().ignored(),
      fieldWithPath(PARENT_COMMENT_ANCHOR).optional().ignored(),
      };

  private static final FieldDescriptor[] COMMENT_ARR_fIELDS
      = Stream.concat(Stream.of(fieldWithPath("[]").description("List of comments")),
                      Arrays.stream(COMMENTS_FIELDS).map(DocUtil::toArrayFieldDescriptor))
              .toArray(FieldDescriptor[]::new);

  public static final ResponseFieldsSnippet COMMENT_RESPONSE_FIELDS = responseFields(COMMENTS_FIELDS);
  public static final ResponseFieldsSnippet COMMENTS_RESPONSE_FIELDS
      = responseFields(COMMENT_ARR_fIELDS);

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

  private DocUtil() {
    // do not instantiate
  }

  public static FieldDescriptor[] pageableFields() {
    return Stream
        .of(fieldWithPath("pageable").ignored(),
            fieldWithPath("pageable.sort").ignored(),
            fieldWithPath("pageable.sort.*").ignored(),
            fieldWithPath("pageable.pageNumber").ignored(),
            fieldWithPath("pageable.pageSize").ignored(),
            fieldWithPath("pageable.offset").ignored(),
            fieldWithPath("pageable.paged").ignored(),
            fieldWithPath("pageable.unpaged").ignored(),

            fieldWithPath("last").description("true if the current page is the last one, false if no"),
            fieldWithPath("totalPages").description("Total number of pages"),
            fieldWithPath("totalElements").description("Total number of elements"),
            fieldWithPath("first").description("true if the current page is the first one, false if no"),
            fieldWithPath("number").description("The number of the current page, starting by 0"),
            fieldWithPath("numberOfElements").description("Number of elements per page"),
            fieldWithPath("size").description("Same as numberOfElements"),

            fieldWithPath("sort").description("Sorting parameters"),
            fieldWithPath("sort.sorted")
                .description("true if the current elements are sorted, false if no"),
            fieldWithPath("sort.unsorted")
                .description("true if the current elements are not sorted, false if they are"),
            fieldWithPath("sort.empty").ignored(),
            fieldWithPath("empty").ignored())
        .toArray(FieldDescriptor[]::new);
  }
}
