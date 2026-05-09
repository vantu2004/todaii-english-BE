package com.todaii.english.shared.dto;

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

  private ToeicTestDTO test;
}
