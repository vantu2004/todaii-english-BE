package com.todaii.english.shared.request.server;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class ArticleParagraphRequest {

	private Long id;

	@NotNull(message = "Paragraph order (paraOrder) is required")
	@Min(value = 1, message = "Paragraph order (paraOrder) must be at least 1")
	private Integer paraOrder;

	@Length(min = 10, message = "English text (textEn) must be at least 10 characters")
	private String textEn;

	@Length(min = 10, message = "Vietnamese system text (textViSystem) must be at least 10 characters")
	private String textViSystem;
}
