package com.todaii.english.shared.response;

import java.util.List;

import com.todaii.english.shared.dto.toeic.ToeicTestSessionDTO;
import com.todaii.english.shared.dto.toeic.ToeicUserAnswerDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToeicSessionDetailsResponse {
  private ToeicTestSessionDTO session;
  private List<ToeicUserAnswerDTO> answers;
}
