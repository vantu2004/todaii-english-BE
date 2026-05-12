package com.todaii.english.core.port;

import java.util.List;

public interface GgTranslatePort {
  List<String> translateText(String tagetLanguage, List<String> texts);
}
