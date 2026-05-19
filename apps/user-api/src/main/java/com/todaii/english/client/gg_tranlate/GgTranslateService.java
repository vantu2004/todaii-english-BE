package com.todaii.english.client.gg_tranlate;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.port.GgTranslatePort;
import com.todaii.english.core.port.UsageStatisticPort;
import com.todaii.english.shared.request.client.GgTranslateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GgTranslateService {
  private final GgTranslatePort ggTranslationPort;
  private final UsageStatisticPort usageStatisticPort;

  public List<String> translateText(Long currentUserId, GgTranslateRequest ggTranslateRequest) {
    List<String> response =
        ggTranslationPort.translateText(
            ggTranslateRequest.getSourceLanguage(),
            ggTranslateRequest.getTargetLanguage(),
            ggTranslateRequest.getTexts());

    UsageStatistic ggTranslateStat =
        usageStatisticPort.createGgTranslateStat(
            currentUserId, ggTranslateRequest.getTotalCharacterCount());
    usageStatisticPort.createUsageStatistic(ggTranslateStat);

    return response;
  }
}
