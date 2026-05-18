package com.todaii.english.shared.response;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
public class DashboardChartDTO {
  private Long userLoginQuantity;
  private Long adminLoginQuantity;
  private AiRequest aiRequest;
  private Long mailSentQuantity;
  private Long dictionaryRequest;
  private Long newsApiQuantity;
  private YoutubeRequest youtubeRequest;
  private Long cloudinaryUploadQuantity;
  private GgTranslate ggTranslateRequest;
  private LocalDate startDate;
  private LocalDate endDate;

  @Getter
  @Setter
  public static class AiRequest {
    private String model;
    private Long aiQuantity;
    private Long inputToken;
    private Long outputToken;
    private Long totalToken;
  }

  @Getter
  @Setter
  public static class DictionaryRequest {
    private Long dictionaryApiQuantity;
    private Long todaiiDictQuantity;
  }

  @Getter
  @Setter
  public static class YoutubeRequest {
    private Long youtubeQuantity;
    private Long quota;
  }

  @Getter
  @Setter
  public static class GgTranslate {
    private Long ggTranslateQuantity;
    private Long charQuantity;
  }
}
