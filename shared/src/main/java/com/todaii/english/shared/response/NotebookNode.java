package com.todaii.english.shared.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotebookNode {
	private Long id;
	private String name;
	private boolean isFolder;
	private String content;
	private List<NotebookNode> children = new ArrayList<>();
}
