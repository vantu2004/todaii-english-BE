package com.todaii.english.client.learning.service;

import java.util.List;
import java.util.Set;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import com.todaii.english.client.article.ArticleRepository;
import com.todaii.english.client.toeic_test.TestRepository;
import com.todaii.english.client.video.VideoRepository;
import com.todaii.english.client.vocabulary.VocabDeckRepository;
import com.todaii.english.core.entity.DictionaryWord;
import com.todaii.english.core.entity.article.Article;
import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.entity.video.Video;
import com.todaii.english.core.entity.vocabulary.VocabDeck;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI Tool Calling class cho AI Coach. Không phải Spring Bean — mỗi lần generate plan sẽ khởi
 * tạo instance mới với userId cụ thể.
 *
 * <p>AI sẽ phân tích dữ liệu học viên rồi tự quyết định gọi tools nào, bao nhiêu lần.
 */
@Slf4j
@RequiredArgsConstructor
public class StudyPlanTools {
  private final Long userId;
  private final String cefrLevel;
  private final Integer weakestPart;

  private final ArticleRepository articleRepository;
  private final VideoRepository videoRepository;
  private final VocabDeckRepository vocabDeckRepository;
  private final TestRepository testRepository;

  /** Record trả về cho AI — chứa thông tin content cần thiết. */
  public record ContentInfo(
      Long id, String title, String cefrLevel, Integer estimatedMinutes, boolean inProgress) {}

  @Tool(
      description =
          "Get recommended articles for the student based on their CEFR level. "
              + "Excludes articles the student has already completed. "
              + "Returns list of articles with id, title, cefrLevel, estimatedMinutes.")
  public List<ContentInfo> getRecommendedArticles(
      @ToolParam(description = "Number of articles to get, between 1 and 5") int limit) {
    log.info("🔧 Tool called: getRecommendedArticles(limit={})", limit);
    int safeLimit = Math.max(1, Math.min(limit, 5));

    List<Article> articles =
        articleRepository.findRandomByCefrLevelExcludeCompleted(userId, cefrLevel, safeLimit);

    // fallback nếu không đủ bài theo CEFR
    if (articles.size() < safeLimit) {
      articles = articleRepository.findRandomArticles(safeLimit);
    }

    return articles.stream()
        .map(
            a ->
                new ContentInfo(
                    a.getId(),
                    a.getTitle(),
                    a.getCefrLevel() != null ? a.getCefrLevel().name() : null,
                    (int) Math.ceil(a.getEstimatedReadingTime() / 60.0),
                    false))
        .toList();
  }

  @Tool(
      description =
          "Get articles the student started reading but hasn't finished yet. "
              + "Good for suggesting continuation of learning. "
              + "Returns list with inProgress=true.")
  public List<ContentInfo> getInProgressArticles(
      @ToolParam(description = "Number of in-progress articles to get, between 1 and 3")
          int limit) {
    log.info("🔧 Tool called: getInProgressArticles(limit={})", limit);
    int safeLimit = Math.max(1, Math.min(limit, 3));

    List<Article> articles = articleRepository.findInProgressByUser(userId, safeLimit);

    return articles.stream()
        .map(
            a ->
                new ContentInfo(
                    a.getId(),
                    a.getTitle(),
                    a.getCefrLevel() != null ? a.getCefrLevel().name() : null,
                    (int) Math.ceil(a.getEstimatedReadingTime() / 60.0),
                    true))
        .toList();
  }

  @Tool(
      description =
          "Get recommended videos for the student based on their CEFR level. "
              + "Excludes videos the student has already completed.")
  public List<ContentInfo> getRecommendedVideos(
      @ToolParam(description = "Number of videos to get, between 1 and 5") int limit) {
    log.info("🔧 Tool called: getRecommendedVideos(limit={})", limit);
    int safeLimit = Math.max(1, Math.min(limit, 5));

    List<Video> videos =
        videoRepository.findRandomByCefrLevelExcludeCompleted(userId, cefrLevel, safeLimit);

    if (videos.size() < safeLimit) {
      videos = videoRepository.findRandomVideos(safeLimit);
    }

    return videos.stream()
        .map(
            v ->
                new ContentInfo(
                    v.getId(),
                    v.getTitle(),
                    v.getCefrLevel() != null ? v.getCefrLevel().name() : null,
                    (int) Math.ceil(v.getEstimatedWatchTime() / 60.0),
                    false))
        .toList();
  }

  @Tool(
      description =
          "Get videos the student started watching but hasn't finished yet. "
              + "Good for suggesting continuation. Returns list with inProgress=true.")
  public List<ContentInfo> getInProgressVideos(
      @ToolParam(description = "Number of in-progress videos to get, between 1 and 3") int limit) {
    log.info("🔧 Tool called: getInProgressVideos(limit={})", limit);
    int safeLimit = Math.max(1, Math.min(limit, 3));

    List<Video> videos = videoRepository.findInProgressByUser(userId, safeLimit);

    return videos.stream()
        .map(
            v ->
                new ContentInfo(
                    v.getId(),
                    v.getTitle(),
                    v.getCefrLevel() != null ? v.getCefrLevel().name() : null,
                    (int) Math.ceil(v.getEstimatedWatchTime() / 60.0),
                    true))
        .toList();
  }

  @Tool(description = "Get vocabulary decks for the student to study, matched to their CEFR level.")
  public List<ContentInfo> getRecommendedVocabDecks(
      @ToolParam(description = "Number of vocabulary decks to get, between 1 and 3") int limit) {
    log.info("🔧 Tool called: getRecommendedVocabDecks(limit={})", limit);
    int safeLimit = Math.max(1, Math.min(limit, 3));

    List<VocabDeck> decks = vocabDeckRepository.findRandomByCefrLevel(cefrLevel, safeLimit);

    if (decks.size() < safeLimit) {
      decks = vocabDeckRepository.findRandomDecks(safeLimit);
    }

    return decks.stream()
        .map(
            d ->
                new ContentInfo(
                    d.getId(),
                    d.getName(),
                    d.getCefrLevel() != null ? d.getCefrLevel().name() : null,
                    calculateVocabDeckEstimatedMinutes(d.getWords()),
                    false))
        .toList();
  }

  private int calculateVocabDeckEstimatedMinutes(Set<DictionaryWord> words) {
    if (words == null || words.isEmpty()) {
      return 1;
    }

    int totalSeconds = words.size() * 15;
    return Math.max(1, (int) Math.ceil(totalSeconds / 60.0));
  }

  @Tool(
      description =
          "Get TOEIC practice tests. If the student has identified weak parts, "
              + "returns tests that focus on those weak parts. "
              + "Otherwise returns random published tests.")
  public List<ContentInfo> getRecommendedTests(
      @ToolParam(description = "Number of tests to get, between 1 and 3") int limit) {
    log.info("🔧 Tool called: getRecommendedTests(limit={})", limit);
    int safeLimit = Math.max(1, Math.min(limit, 3));

    List<ToeicTest> tests;
    if (weakestPart != null) {
      tests = testRepository.findRandomPublishedByPartNumber(weakestPart, safeLimit);
      if (tests.isEmpty()) {
        tests = testRepository.findRandomPublished(safeLimit);
      }
    } else {
      tests = testRepository.findRandomPublished(safeLimit);
    }

    return tests.stream()
        .map(
            t ->
                new ContentInfo(
                    t.getId(),
                    t.getTitle(),
                    null,
                    t.getDuration(), // duration in minutes
                    false))
        .toList();
  }
}
