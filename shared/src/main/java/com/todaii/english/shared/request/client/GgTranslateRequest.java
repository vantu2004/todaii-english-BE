package com.todaii.english.shared.request.client;

import java.util.List;

public record GgTranslateRequest(String targetLanguage, List<String> texts) {}
