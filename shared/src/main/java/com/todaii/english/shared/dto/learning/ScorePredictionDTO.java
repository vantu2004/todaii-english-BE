package com.todaii.english.shared.dto.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ScorePredictionDTO {
  private Integer avgScore; // điểm trung bình 3 bài gần nhất
  private Integer predictedScore; // avgScore + trendBonus
  private Integer trendBonus; // xu hướng tăng/giảm
  private Integer totalTestsTaken; // tổng số bài FULL_TEST đã làm
  private String trend; // IMPROVING / STABLE / DECLINING
}
