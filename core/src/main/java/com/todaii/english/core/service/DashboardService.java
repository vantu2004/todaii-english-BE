package com.todaii.english.core.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.repository.DashboardRepository;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.AiProvider;
import com.todaii.english.shared.enums.UsageType;
import com.todaii.english.shared.response.DashboardChartDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
  private final DashboardRepository dashboardRepository;

  public List<DashboardChartDTO> getChartByActorType(
      ActorType actorType, LocalDate startDate, LocalDate endDate) {
    List<UsageStatistic> stats =
        dashboardRepository.findByActorTypeAndCreatedAtBetween(actorType, startDate, endDate);

    return aggregateStatsByDate(stats, startDate, endDate);
  }

  public List<DashboardChartDTO> getChartByActorId(
      ActorType actorType, Long actorId, LocalDate startDate, LocalDate endDate) {
    List<UsageStatistic> stats =
        dashboardRepository.findByActorTypeAndActorIdAndCreatedAtBetween(
            actorType, actorId, startDate, endDate);
    return aggregateStatsByDate(stats, startDate, endDate);
  }

  /**
   * Hàm dùng chung để nhóm dữ liệu theo từng ngày. Đảm bảo ngày nào không có dữ liệu vẫn trả về
   * object chứa giá trị 0.
   */
  private List<DashboardChartDTO> aggregateStatsByDate(
      List<UsageStatistic> stats, LocalDate startDate, LocalDate endDate) {
    // Nhóm dữ liệu theo ngày
    Map<LocalDate, List<UsageStatistic>> statsByDate =
        stats.stream().collect(Collectors.groupingBy(UsageStatistic::getCreatedAt));

    List<DashboardChartDTO> chartData = new ArrayList<>();
    long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);

    for (int i = 0; i <= numOfDays; i++) {
      LocalDate currentDate = startDate.plusDays(i);
      List<UsageStatistic> dailyStats = statsByDate.getOrDefault(currentDate, new ArrayList<>());
      chartData.add(buildDailyChartDTO(currentDate, dailyStats));
    }

    return chartData;
  }

  // Helper ánh xạ dữ liệu UsageStatistic -> DashboardChartDTO
  // Khóa tạm thời dùng để gom nhóm các bản ghi AI theo cặp (Provider, Model)
  private record AiGroupKey(AiProvider provider, String model) {}

  // Helper ánh xạ dữ liệu UsageStatistic -> DashboardChartDTO theo từng ngày
  private DashboardChartDTO buildDailyChartDTO(LocalDate date, List<UsageStatistic> dailyStats) {
    DashboardChartDTO chartDTO =
        DashboardChartDTO.builder()
            .date(date)
            .aiRequests(new ArrayList<>()) // Khởi tạo list trống cho dữ liệu AI
            .build();

    // BƯỚC 1: Lọc riêng các event AI_REQUEST và gom nhóm theo (AiProvider, Model)
    Map<AiGroupKey, List<UsageStatistic>> aiStatsGrouped =
        dailyStats.stream()
            .filter(stat -> stat.getUsageType() == UsageType.AI_REQUEST)
            .collect(
                Collectors.groupingBy(
                    stat -> new AiGroupKey(stat.getAiProvider(), stat.getModel())));

    // BƯỚC 2: Cộng dồn dữ liệu của từng nhóm AI và add vào list aiRequests
    aiStatsGrouped.forEach(
        (key, stats) -> {
          long totalQty =
              stats.stream().mapToLong(s -> s.getQuantity() != null ? s.getQuantity() : 0).sum();
          long totalInput =
              stats.stream()
                  .mapToLong(s -> s.getInputToken() != null ? s.getInputToken() : 0)
                  .sum();
          long totalOutput =
              stats.stream()
                  .mapToLong(s -> s.getOutputToken() != null ? s.getOutputToken() : 0)
                  .sum();
          long totalTokens =
              stats.stream()
                  .mapToLong(s -> s.getTotalToken() != null ? s.getTotalToken() : 0)
                  .sum();

          chartDTO
              .getAiRequests()
              .add(
                  DashboardChartDTO.AiRequestDetail.builder()
                      .aiProvider(key.provider())
                      .model(key.model())
                      .quantity(totalQty)
                      .inputToken(totalInput)
                      .outputToken(totalOutput)
                      .totalToken(totalTokens)
                      .build());
        });

    // BƯỚC 3: Xử lý các loại request thông thường khác (Bỏ qua AI_REQUEST vì đã xử lý ở Bước 1 & 2)
    for (UsageStatistic stat : dailyStats) {
      long qty = stat.getQuantity() != null ? stat.getQuantity() : 0;

      switch (stat.getUsageType()) {
        case LOGIN_REQUEST -> chartDTO.setLoginQuantity(chartDTO.getLoginQuantity() + qty);
        case MAIL_SEND -> chartDTO.setMailSentQuantity(chartDTO.getMailSentQuantity() + qty);
        case NEWS_API_REQUEST -> chartDTO.setNewsApiQuantity(chartDTO.getNewsApiQuantity() + qty);
        case CLOUDINARY_UPLOAD -> chartDTO.setCloudinaryUploadQuantity(
            chartDTO.getCloudinaryUploadQuantity() + qty);

        case DICTIONARY_API_REQUEST -> chartDTO
            .getDictionaryRequest()
            .setDictionaryApiQuantity(
                chartDTO.getDictionaryRequest().getDictionaryApiQuantity() + qty);
        case TODAII_DICT_REQUEST -> chartDTO
            .getDictionaryRequest()
            .setTodaiiDictQuantity(chartDTO.getDictionaryRequest().getTodaiiDictQuantity() + qty);

        case YOUTUBE_SEARCH -> {
          chartDTO
              .getYoutubeRequest()
              .setQuantity(chartDTO.getYoutubeRequest().getQuantity() + qty);
          chartDTO
              .getYoutubeRequest()
              .setQuota(chartDTO.getYoutubeRequest().getQuota() + stat.getQuota());
        }
        case GOOGLE_TRANSLATE_REQUEST -> {
          chartDTO
              .getGgTranslateRequest()
              .setQuantity(chartDTO.getGgTranslateRequest().getQuantity() + qty);
          chartDTO
              .getGgTranslateRequest()
              .setCharQuantity(
                  chartDTO.getGgTranslateRequest().getCharQuantity() + stat.getCharQuantity());
        }
        default -> {} // Các request loại AI_REQUEST đã được xử lý phía trên
      }
    }

    return chartDTO;
  }
}
