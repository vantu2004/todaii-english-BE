package com.todaii.english.shared.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalErrorDTO {
	private LocalDateTime timestamp;
	private int status;
	private String path;
	private List<String> errors = new ArrayList<String>();

	public void addError(String error) {
		errors.add(error);
	}
}