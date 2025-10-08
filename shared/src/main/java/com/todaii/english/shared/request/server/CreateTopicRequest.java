package com.todaii.english.shared.request.server;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;
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

	@JsonProperty("topic_type")
	private TopicType topicType;
}
