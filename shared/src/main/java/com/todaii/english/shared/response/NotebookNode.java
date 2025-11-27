package com.todaii.english.shared.response;

import java.util.ArrayList;
import java.util.List;

import com.todaii.english.shared.enums.NotebookType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotebookNode {
	private Long id;
	private String name;
	private NotebookType type;
	private List<NotebookNode> children = new ArrayList<>();
}
