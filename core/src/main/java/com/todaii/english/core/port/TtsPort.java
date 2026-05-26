package com.todaii.english.core.port;

public interface TtsPort {
  byte[] call(String text);
}
