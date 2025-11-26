package com.todaii.english.shared.request.client;

import org.hibernate.validator.constraints.Length;

import com.todaii.english.shared.enums.NotebookType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotebookRequest {
	@NotBlank(message = "Name cannot be blank")
	@Length(max = 191, message = "Name must not exceed 191 characters")
	private String name;

	@NotNull(message = "Type cannot be blank")
	private NotebookType type;

	private Long parentId;
}
