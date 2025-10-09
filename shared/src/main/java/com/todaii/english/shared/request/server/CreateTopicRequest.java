package com.todaii.english.shared.request.server;

import org.hibernate.validator.constraints.Length;

import com.todaii.english.shared.enums.TopicType;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTopicRequest {
	@NotBlank
	@Length(max = 191)
	private String name;

	private TopicType topicType;
}
