package com.todaii.english.core.port;

import java.util.List;

import com.todaii.english.shared.enums.ActorType;

public interface VocabExtractionPort {
  List<String> vocabExtraction(String text, String words, Long actorId, ActorType actorType);
}
