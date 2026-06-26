package com.todaii.english.shared.dto.learning;

import java.util.List;

import com.todaii.english.shared.dto.toeic.ToeicTestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TestRecommendationDTO {
  private String message; // message gợi ý (vd: "Vì bạn đang yếu Part 7...")
  private Integer weakestPart; // Part yếu nhất
  private List<ToeicTestDTO> tests;
}
