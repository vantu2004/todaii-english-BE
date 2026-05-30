package com.todaii.english.shared.dto.dashboard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.todaii.english.shared.enums.AiProvider;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardChartDTO {
  private LocalDate date;

  @Builder.Default private Long loginQuantity = 0L;
  @Builder.Default private Long mailSentQuantity = 0L;
  @Builder.Default private Long newsApiQuantity = 0L;
  @Builder.Default private Long cloudinaryUploadQuantity = 0L;

  // Các object con chứa logic chi tiết
  @Builder.Default private List<AiRequestDetail> aiRequests = new ArrayList<>();

  @Builder.Default
  private DictionaryRequest dictionaryRequest = DictionaryRequest.builder().build();

  @Builder.Default private YoutubeRequest youtubeRequest = YoutubeRequest.builder().build();
  @Builder.Default private GgTranslate ggTranslateRequest = GgTranslate.builder().build();

  @Getter
  @Setter
  @Builder
  public static class AiRequestDetail {
    @Builder.Default private Long quantity = 0L;
    @Builder.Default private Long inputToken = 0L;
    @Builder.Default private Long outputToken = 0L;
    @Builder.Default private Long totalToken = 0L;
    private AiProvider aiProvider;
    private String model;
  }

  @Getter
  @Setter
  @Builder
  public static class DictionaryRequest {
    @Builder.Default private Long dictionaryApiQuantity = 0L;
    @Builder.Default private Long todaiiDictQuantity = 0L;
  }

  @Getter
  @Setter
  @Builder
  public static class YoutubeRequest {
    @Builder.Default private Long quantity = 0L;
    @Builder.Default private Long quota = 0L;
  }

  @Getter
  @Setter
  @Builder
  public static class GgTranslate {
    @Builder.Default private Long quantity = 0L;
    @Builder.Default private Long charQuantity = 0L;
  }
}
