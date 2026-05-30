package com.todaii.english.server.dashboard;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.todaii.english.core.repository.DictionaryRepository;
import com.todaii.english.server.admin.AdminRepository;
import com.todaii.english.server.article.ArticleRepository;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.server.user.UserRepository;
import com.todaii.english.server.video.VideoRepository;
import com.todaii.english.server.vocabulary.VocabDeckRepository;
import com.todaii.english.shared.constants.ApiUrl;
import com.todaii.english.shared.dto.dashboard.DashboardSummaryDTO;
import com.todaii.english.shared.dto.dashboard.UpstashDashboardStatDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServerDashboardService {
  private final AdminRepository adminRepository;
  private final ArticleRepository articleRepository;
  private final DictionaryRepository dictionaryRepository;
  private final TestRepository testRepository;
  private final UserRepository userRepository;
  private final VideoRepository videoRepository;
  private final VocabDeckRepository vocabDeckRepository;
  private final WebClient webClient;

  @Value("${upstash.database-id}")
  String databaseId;

  @Value("${upstash.username}")
  String username;

  @Value("${upstash.password}")
  String password;

  public DashboardSummaryDTO getDashboardSummary() {
    return new DashboardSummaryDTO(
        adminRepository.countByIsDeletedFalse(),
        articleRepository.count(),
        dictionaryRepository.count(),
        testRepository.count(),
        userRepository.countByIsDeletedFalse(),
        videoRepository.count(),
        vocabDeckRepository.count());
  }

  public UpstashDashboardStatDTO getStats() {
    String credentials = username + ":" + password;

    String basicAuth =
        "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

    return webClient
        .get()
        .uri(String.format(ApiUrl.UPSTASH_STAT_URL, databaseId))
        .header("Authorization", basicAuth)
        .retrieve()
        .bodyToMono(UpstashDashboardStatDTO.class)
        .block();
  }
}
