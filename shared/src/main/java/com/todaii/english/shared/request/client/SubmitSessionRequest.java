package com.todaii.english.shared.request.client;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitSessionRequest {
  private Integer timeSpent;
  private List<AnswerRequest> answers;
}
