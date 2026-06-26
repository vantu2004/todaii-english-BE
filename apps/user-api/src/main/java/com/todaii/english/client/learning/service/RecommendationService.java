package com.todaii.english.client.learning.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.todaii.english.client.article.ArticleRepository;
import com.todaii.english.client.toeic_test.TestRepository;
import com.todaii.english.client.video.VideoRepository;
import com.todaii.english.core.entity.article.Article;
import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.entity.video.Video;
import com.todaii.english.shared.dto.learning.TestRecommendationDTO;
import com.todaii.english.shared.dto.toeic.ToeicTestDTO;
import com.todaii.english.shared.enums.CefrLevel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {
  private final AnalyticsService analyticsService;
  private final ArticleRepository articleRepository;
  private final VideoRepository videoRepository;
  private final TestRepository testRepository;
  private final ModelMapper modelMapper;

  private static final int RECOMMEND_LIMIT = 3;

  /**
   * Map điểm TOEIC sang CEFR Level.
   *
   * <ul>
   *   <li>< 225 -> A1
   *   <li>< 550 -> A2
   *   <li>< 785 -> B1
   *   <li>< 945 -> C1
   *   <li>>= 945 -> C2
   * </ul>
   */
  public static CefrLevel getCefrLevel(int toeicScore) {
    if (toeicScore < 225) return CefrLevel.A1;
    if (toeicScore < 550) return CefrLevel.A2;
    if (toeicScore < 785) return CefrLevel.B1;
    if (toeicScore < 945) return CefrLevel.C1;
    return CefrLevel.C2;
  }

  /** Gợi ý articles theo CEFR level của user. Nếu không đủ bài theo CEFR thì fallback random. */
  public List<Article> recommendArticles(Long userId) {
    int currentScore = analyticsService.getCurrentScore(userId);
    CefrLevel cefrLevel = getCefrLevel(currentScore);

    List<Article> articles =
        articleRepository.findRandomByCefrLevel(cefrLevel.name(), RECOMMEND_LIMIT);

    // Fallback nếu không đủ bài
    if (articles.size() < RECOMMEND_LIMIT) {
      articles = articleRepository.findRandomArticles(RECOMMEND_LIMIT);
    }

    return articles;
  }

  /** Gợi ý videos theo CEFR level của user. Nếu không đủ video theo CEFR thì fallback random. */
  public List<Video> recommendVideos(Long userId) {
    int currentScore = analyticsService.getCurrentScore(userId);
    CefrLevel cefrLevel = getCefrLevel(currentScore);

    List<Video> videos = videoRepository.findRandomByCefrLevel(cefrLevel.name(), RECOMMEND_LIMIT);

    // Fallback nếu không đủ video
    if (videos.size() < RECOMMEND_LIMIT) {
      videos = videoRepository.findRandomVideos(RECOMMEND_LIMIT);
    }

    return videos;
  }

  /** Gợi ý tests dựa trên Part yếu nhất của user. Trả về kèm message gợi ý. */
  public TestRecommendationDTO recommendTests(Long userId) {
    Integer weakestPart = analyticsService.findWeakestPart(userId);

    List<ToeicTest> tests;
    String message;

    if (weakestPart != null) {
      tests = testRepository.findRandomPublishedByPartNumber(weakestPart, RECOMMEND_LIMIT);
      message = "Vì bạn đang yếu Part " + weakestPart + ", hãy thử sức với đề này nhé!";

      // Fallback nếu không tìm được test theo part
      if (tests.isEmpty()) {
        tests = testRepository.findRandomPublished(RECOMMEND_LIMIT);
        message = "Hãy thử sức với các đề thi này để cải thiện điểm số nhé!";
      }
    } else {
      tests = testRepository.findRandomPublished(RECOMMEND_LIMIT);
      message = "Hãy bắt đầu luyện đề để hệ thống phân tích điểm yếu của bạn!";
    }

    List<ToeicTestDTO> testDTOs =
        tests.stream()
            .map(test -> modelMapper.map(test, ToeicTestDTO.class))
            .collect(Collectors.toList());

    return TestRecommendationDTO.builder()
        .message(message)
        .weakestPart(weakestPart)
        .tests(testDTOs)
        .build();
  }
}
