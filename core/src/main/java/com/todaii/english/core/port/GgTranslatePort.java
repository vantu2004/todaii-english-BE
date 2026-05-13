package com.todaii.english.core.port;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface GgTranslatePort {
  List<String> translateText(String sourceLanguage, String tagetLanguage, List<String> texts);

  byte[] translateDocument(String sourceLanguage, String targetLanguage, MultipartFile file);
}
