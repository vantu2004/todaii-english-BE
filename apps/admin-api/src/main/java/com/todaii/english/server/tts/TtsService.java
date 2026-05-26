package com.todaii.english.server.tts;

import org.springframework.stereotype.Service;

import com.todaii.english.core.port.TtsPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TtsService {
  private final TtsPort ttsPort;

  public byte[] call(String text) {
    return ttsPort.call(text);
  }
}
