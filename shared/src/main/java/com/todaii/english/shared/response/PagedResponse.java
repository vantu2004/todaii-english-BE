package com.todaii.english.shared.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Paged response with metadata for pagination")
public class PagedResponse<T> {

    @ArraySchema(
        arraySchema = @Schema(description = "List of items of type T")
    )
    private final List<T> content;

    @Schema(description = "Current page number", example = "1")
    private final int page;

    @Schema(description = "Number of items per page", example = "10")
    private final int size;

    @Schema(description = "Total number of elements", example = "50")
    private final long totalElements;

    @Schema(description = "Total number of pages", example = "5")
    private final int totalPages;

    @Schema(description = "Is first page", example = "true")
    private final boolean first;

    @Schema(description = "Is last page", example = "false")
    private final boolean last;

    @Schema(description = "Field used for sorting", example = "id")
    private final String sortBy;

    @Schema(description = "Sort direction", example = "desc")
    private final String direction;
}
