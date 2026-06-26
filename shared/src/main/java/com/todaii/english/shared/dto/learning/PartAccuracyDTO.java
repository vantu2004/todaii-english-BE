package com.todaii.english.shared.dto.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PartAccuracyDTO {
  private Integer part;
  private Long total;
  private Long correct;
  private Double accuracy; // phần trăm đúng (0.0 - 100.0)
}
