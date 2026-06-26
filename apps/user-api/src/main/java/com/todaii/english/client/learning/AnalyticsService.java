package com.todaii.english.client.learning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.toeic_test_session.ToeicTestSessionRepository;
import com.todaii.english.client.toeic_test_session.ToeicUserAnswerRepository;
import com.todaii.english.core.entity.learning.UserLearningProfile;
import com.todaii.english.core.entity.toeic.ToeicTestSession;
import com.todaii.english.shared.dto.learning.PartAccuracyDTO;
import com.todaii.english.shared.dto.learning.ScorePredictionDTO;
import com.todaii.english.shared.enums.ToeicSessionMode;
import com.todaii.english.shared.enums.ToeicSessionStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
  private final ToeicUserAnswerRepository toeicUserAnswerRepository;
  private final ToeicTestSessionRepository toeicTestSessionRepository;
  private final UserLearningProfileRepository userLearningProfileRepository;

  /**
   * Tính % câu đúng theo từng Part (1-7) cho radar chart.
   *
   * @return danh sách 7 phần tử (Part 1 -> Part 7) với accuracy tương ứng
   */
  public List<PartAccuracyDTO> getWeaknessAnalysis(Long userId) {
    List<Object[]> rawData = toeicUserAnswerRepository.getAccuracyByPart(userId);

    // Chuyển rawData thành Map để tra cứu nhanh
    Map<Integer, Object[]> dataMap =
        rawData.stream()
            .collect(
                Collectors.toMap(
                    row -> ((Number) row[0]).intValue(), // partNumber
                    row -> row));

    List<PartAccuracyDTO> result = new ArrayList<>();

    // Đảm bảo luôn trả về đủ 7 Part, kể cả khi chưa có dữ liệu
    for (int part = 1; part <= 7; part++) {
      Object[] row = dataMap.get(part);
      if (row != null) {
        long total = ((Number) row[1]).longValue();
        long correct = ((Number) row[2]).longValue();
        double accuracy = total > 0 ? Math.round((double) correct / total * 1000.0) / 10.0 : 0.0;
        result.add(
            PartAccuracyDTO.builder()
                .part(part)
                .total(total)
                .correct(correct)
                .accuracy(accuracy)
                .build());
      } else {
        result.add(
            PartAccuracyDTO.builder().part(part).total(0L).correct(0L).accuracy(0.0).build());
      }
    }

    return result;
  }

  /**
   * Tìm Part yếu nhất (accuracy thấp nhất) trong các part đã có dữ liệu.
   *
   * @return part number có accuracy thấp nhất, hoặc null nếu chưa có dữ liệu
   */
  public Integer findWeakestPart(Long userId) {
    List<PartAccuracyDTO> analysis = getWeaknessAnalysis(userId);

    // Chỉ xét các part đã có dữ liệu (total > 0)
    return analysis.stream()
        .filter(p -> p.getTotal() > 0)
        .min((a, b) -> Double.compare(a.getAccuracy(), b.getAccuracy()))
        .map(PartAccuracyDTO::getPart)
        .orElse(null);
  }

  /**
   * Dự đoán điểm TOEIC dựa trên 3 bài test FULL_TEST COMPLETED gần nhất. Cập nhật currentScore
   * trong UserLearningProfile.
   */
  @Transactional
  public ScorePredictionDTO getScorePrediction(Long userId) {
    List<ToeicTestSession> sessions =
        toeicTestSessionRepository.findTop3ByUserIdAndStatusAndModeOrderByCompletedAtDesc(
            userId, ToeicSessionStatus.COMPLETED, ToeicSessionMode.FULL_TEST);

    if (sessions.isEmpty()) {
      return ScorePredictionDTO.builder()
          .avgScore(0)
          .predictedScore(0)
          .trendBonus(0)
          .totalTestsTaken(0)
          .trend("NO_DATA")
          .build();
    }

    // sessions đang sắp xếp DESC (mới nhất trước), reverse lại thành ASC (cũ nhất trước)
    List<ToeicTestSession> orderedSessions = new ArrayList<>(sessions);
    Collections.reverse(orderedSessions);

    // Tính điểm trung bình
    int totalScore = orderedSessions.stream().mapToInt(ToeicTestSession::getTotalScore).sum();
    int avgScore = totalScore / orderedSessions.size();

    // Tính trend bonus
    int trendBonus = 0;
    String trend = "STABLE";

    if (orderedSessions.size() >= 2) {
      int oldest = orderedSessions.get(0).getTotalScore();
      int newest = orderedSessions.get(orderedSessions.size() - 1).getTotalScore();
      int diff = newest - oldest;

      trendBonus = (int) Math.round(diff * 0.5);

      if (diff > 0) {
        trend = "IMPROVING";
      } else if (diff < 0) {
        trend = "DECLINING";
      }
    }

    int predictedScore = Math.max(10, Math.min(990, avgScore + trendBonus));

    // Cập nhật currentScore vào UserLearningProfile (nếu có)
    userLearningProfileRepository
        .findByUserId(userId)
        .ifPresent(
            profile -> {
              profile.setCurrentScore(avgScore);
              userLearningProfileRepository.save(profile);
            });

    return ScorePredictionDTO.builder()
        .avgScore(avgScore)
        .predictedScore(predictedScore)
        .trendBonus(trendBonus)
        .totalTestsTaken(orderedSessions.size())
        .trend(trend)
        .build();
  }

  /**
   * Lấy currentScore từ UserLearningProfile. Nếu chưa có, tính từ sessions.
   *
   * @return currentScore hoặc 0
   */
  public int getCurrentScore(Long userId) {
    return userLearningProfileRepository
        .findByUserId(userId)
        .map(UserLearningProfile::getCurrentScore)
        .filter(score -> score != null && score > 0)
        .orElseGet(
            () -> {
              // Tính từ sessions nếu profile chưa có currentScore
              ScorePredictionDTO prediction = getScorePrediction(userId);
              return prediction.getAvgScore();
            });
  }
}
