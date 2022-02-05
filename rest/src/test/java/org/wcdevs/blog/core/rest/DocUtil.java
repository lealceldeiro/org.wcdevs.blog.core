package org.wcdevs.blog.core.rest;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.util.Arrays;
import java.util.stream.Stream;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public final class DocUtil {
  public static final Object STRING_TYPE = JsonFieldType.STRING;

  private DocUtil() {
    // do not instantiate
  }

  public static FieldDescriptor[] pageableFields() {
    return Stream
        .of(fieldWithPath("pageable").description("Pagination information"),
            fieldWithPath("pageable.*").ignored(),
            fieldWithPath("pageable.sort.*").ignored(),

            fieldWithPath("last")
                .description("true if the current page is the last one, false otherwise"),
            fieldWithPath("totalPages").description("Total number of pages"),
            fieldWithPath("totalElements").description("Total number of elements"),
            fieldWithPath("first")
                .description("true if the current page is the first one, false otherwise"),
            fieldWithPath("number").description("The number of the current page, starting by 0"),
            fieldWithPath("numberOfElements").description("Number of elements per page"),
            fieldWithPath("size").description("Same as numberOfElements"),
            fieldWithPath("empty")
                .description("true if the current page has no elements, false otherwise"),

            fieldWithPath("sort").description("Sorting information"),
            fieldWithPath("sort.sorted")
                .description("true if the current elements are sorted, false otherwise"),
            fieldWithPath("sort.unsorted")
                .description("true if the elements aren't sorted, false otherwise"),
            fieldWithPath("sort.empty").ignored())
        .toArray(FieldDescriptor[]::new);
  }

  public static ResponseFieldsSnippet pageableFieldsWith(FieldDescriptor... fieldDescriptors) {
    return responseFields(Stream.concat(Arrays.stream(fieldDescriptors),
                                        Arrays.stream(pageableFields()))
                                .toArray(FieldDescriptor[]::new)
                         );
  }
}
