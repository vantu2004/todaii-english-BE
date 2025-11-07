package com.todaii.english.shared.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PagedResponse<T> {
	private final List<T> content;
	private final int page;
	private final int size;
	private final long totalElements;
	private final int totalPages;
	private final boolean first;
	private final boolean last;
	private final String sortBy;
	private final String direction;
}
