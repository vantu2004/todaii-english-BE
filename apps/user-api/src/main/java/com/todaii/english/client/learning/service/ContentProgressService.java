package com.todaii.english.client.learning.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.article.ArticleRepository;
import com.todaii.english.client.learning.repository.ContentProgressRepository;
import com.todaii.english.client.user.UserRepository;
import com.todaii.english.client.video.VideoRepository;
import com.todaii.english.core.entity.article.Article;
import com.todaii.english.core.entity.learning.ContentProgress;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.core.entity.video.Video;
import com.todaii.english.shared.enums.TopicType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.client.UpsertProgressRequest;
import com.todaii.english.shared.response.ContentProgressResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentProgressService {
  private final ContentProgressRepository contentProgressRepository;
  private final ArticleRepository articleRepository;
  private final VideoRepository videoRepository;
  private final UserRepository userRepository;
  // Thread-safe in-memory rate limiting map for progress updates
  private final Map<String, LocalDateTime> rateLimitMap = new ConcurrentHashMap<>();

  @Transactional(readOnly = true)
  public ContentProgressResponse getProgress(Long userId, Long contentId, TopicType contentType) {
    int estimateTime = getEstimateTime(contentId, contentType);

    Optional<ContentProgress> progressOpt =
        contentProgressRepository.findByUserIdAndContentIdAndContentType(
            userId, contentId, contentType);

    if (progressOpt.isEmpty()) {
      return ContentProgressResponse.builder()
          .contentId(contentId)
          .contentType(contentType)
          .estimateTime(estimateTime)
          .studyTime(0)
          .position(0.0f)
          .completed(false)
          .updatedAt(null)
          .build();
    }

    ContentProgress progress = progressOpt.get();
    return ContentProgressResponse.builder()
        .contentId(progress.getContentId())
        .contentType(progress.getContentType())
        .estimateTime(estimateTime)
        .studyTime(progress.getStudyTime())
        .position(progress.getPosition())
        .completed(progress.getCompleted())
        .updatedAt(progress.getUpdatedAt())
        .build();
  }

  /** Cập nhật tiến độ học tập (Upsert) với anti-cheat clamping và rate limiting. */
  @Transactional
  public ContentProgressResponse upsertProgress(
      Long userId, Long contentId, UpsertProgressRequest request) {
    // 1. Rate limiting check (Tối đa 1 request / 3s / content)
    String rateLimitKey = userId + "_" + request.getContentType() + "_" + contentId;
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime lastRequest = rateLimitMap.get(rateLimitKey);
    if (lastRequest != null && java.time.Duration.between(lastRequest, now).toSeconds() < 3) {
      throw new BusinessException(429, "Please wait at least 3 seconds between progress updates");
    }

    rateLimitMap.put(rateLimitKey, now);

    int estimateTime = getEstimateTime(contentId, request.getContentType());

    ContentProgress progress =
        contentProgressRepository
            .findByUserIdAndContentIdAndContentType(userId, contentId, request.getContentType())
            .orElseGet(
                () -> {
                  User user =
                      userRepository
                          .findById(userId)
                          .orElseThrow(() -> new BusinessException(404, "User not found"));
                  return ContentProgress.builder()
                      .user(user)
                      .contentId(contentId)
                      .contentType(request.getContentType())
                      .studyTime(0)
                      .position(0.0f)
                      .completed(false)
                      .build();
                });

    // 4. Clamping (Anti-cheat)
    int studyTimeDelta = request.getStudyTimeDelta();
    if (progress.getUpdatedAt() != null) {
      long wallClockGap =
          java.time.Duration.between(progress.getUpdatedAt(), LocalDateTime.now()).toSeconds();
      long maxAllowedDelta = wallClockGap + 10; // margin 10s cho độ trễ mạng
      if (studyTimeDelta > maxAllowedDelta) {
        int originalDelta = studyTimeDelta;
        studyTimeDelta = (int) Math.max(0, wallClockGap);

        log.warn(
            "Potential progress cheat/FE bug detected for userId: {}, contentId: {}, contentType:"
                + " {}. Original delta: {}s, Clamped delta: {}s (wallClockGap: {}s)",
            userId,
            contentId,
            request.getContentType(),
            originalDelta,
            studyTimeDelta,
            wallClockGap);
      }
    }

    // 5. Cập nhật studyTime và position
    progress.setStudyTime(progress.getStudyTime() + studyTimeDelta);
    progress.setPosition(request.getPosition());

    // 6. Kiểm tra hoàn thành (completed)
    if (!progress.getCompleted() && progress.getStudyTime() >= estimateTime * 0.8) {
      progress.setCompleted(true);
      progress.setCompletedAt(LocalDateTime.now());
    }

    // 7. Lưu DB và trả về response mới nhất
    ContentProgress saved = contentProgressRepository.save(progress);

    return ContentProgressResponse.builder()
        .contentId(saved.getContentId())
        .contentType(saved.getContentType())
        .estimateTime(estimateTime)
        .studyTime(saved.getStudyTime())
        .position(saved.getPosition())
        .completed(saved.getCompleted())
        .updatedAt(saved.getUpdatedAt())
        .build();
  }

  private int getEstimateTime(Long contentId, TopicType contentType) {
    if (contentType == TopicType.ARTICLE) {
      Article article =
          articleRepository
              .findById(contentId)
              .orElseThrow(() -> new BusinessException(404, "Article not found"));

      return article.getEstimatedReadingTime() != null ? article.getEstimatedReadingTime() : 0;
    } else if (contentType == TopicType.VIDEO) {
      Video video =
          videoRepository
              .findById(contentId)
              .orElseThrow(() -> new BusinessException(404, "Video not found"));

      return video.getEstimatedWatchTime() != null ? video.getEstimatedWatchTime() : 0;
    }

    throw new BusinessException(400, "Unsupported content type: " + contentType);
  }
}
