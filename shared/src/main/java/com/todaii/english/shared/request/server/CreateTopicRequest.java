package com.todaii.english.shared.request.server;

import org.hibernate.validator.constraints.Length;

import com.todaii.english.shared.enums.TopicType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTopicRequest {

	@NotBlank(message = "Topic name cannot be blank")
	@Length(max = 191, message = "Topic name must not exceed 191 characters")
	private String name;

	@NotNull(message = "Topic type cannot be null")
	private TopicType topicType;
}
