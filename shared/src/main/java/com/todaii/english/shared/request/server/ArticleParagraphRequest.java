package com.todaii.english.shared.request.server;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class ArticleParagraphRequest {
	@NotNull
	@Min(1)
	private Integer paraOrder;

	@Length(min = 10)
	private String textEn;

	@Length(min = 10)
	private String textViSystem;
}
