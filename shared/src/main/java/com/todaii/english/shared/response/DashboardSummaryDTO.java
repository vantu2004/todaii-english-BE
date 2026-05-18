package com.todaii.english.shared.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardSummaryDTO {
  private Long totalAdmins;
  private Long totalArticles;
  private Long totalDictionaryEntries;
  private Long totalToeicTest;
  private Long totalUsers;
  private Long totalVideos;
  private Long totalVocabularyDecks;
}
