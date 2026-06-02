package com.todaii.english.shared.dto.toeic;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter
@Setter
public class ToeicPassageDTO {
  private Long id;

  private Integer partNumber;
  private String passageText;
  private String passageTrans;
  private String imageUrl;
  private String audioUrl;
  private LocalDateTime createdAt;
  private List<ToeicQuestionDTO> questions;
}
