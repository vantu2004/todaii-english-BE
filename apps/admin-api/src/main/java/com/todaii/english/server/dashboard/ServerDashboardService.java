package com.todaii.english.server.dashboard;

import org.springframework.stereotype.Service;

import com.todaii.english.core.repository.DictionaryRepository;
import com.todaii.english.server.admin.AdminRepository;
import com.todaii.english.server.article.ArticleRepository;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.server.user.UserRepository;
import com.todaii.english.server.video.VideoRepository;
import com.todaii.english.server.vocabulary.VocabDeckRepository;
import com.todaii.english.shared.response.DashboardSummaryDTO;

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
}
