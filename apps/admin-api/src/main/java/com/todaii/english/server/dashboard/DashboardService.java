package com.todaii.english.server.dashboard;

import org.springframework.stereotype.Service;

import com.todaii.english.server.admin.AdminRepository;
import com.todaii.english.server.article.ArticleRepository;
import com.todaii.english.server.dictionary.DictionaryEntryRepository;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.server.user.UserRepository;
import com.todaii.english.server.video.VideoRepository;
import com.todaii.english.server.vocabulary.VocabDeckRepository;
import com.todaii.english.shared.response.DashboardSummaryDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
  private final AdminRepository adminRepository;
  private final ArticleRepository articleRepository;
  private final DictionaryEntryRepository dictionaryEntryRepository;
  private final TestRepository testRepository;
  private final UserRepository userRepository;
  private final VideoRepository videoRepository;
  private final VocabDeckRepository vocabDeckRepository;

  public DashboardSummaryDTO getDashboardSummary() {
    return new DashboardSummaryDTO(
        adminRepository.countByIsDeletedFalse(),
        articleRepository.count(),
        dictionaryEntryRepository.count(),
        testRepository.count(),
        userRepository.countByIsDeletedFalse(),
        videoRepository.count(),
        vocabDeckRepository.count());
  }
}
