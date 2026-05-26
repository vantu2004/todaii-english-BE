package com.todaii.english.infra.client;

import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechOptions;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.stereotype.Service;

import com.todaii.english.core.port.TtsPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TtsClient implements TtsPort {
  private final TextToSpeechModel textToSpeechModel;

  @Override
  public byte[] call(String text) {
    TextToSpeechOptions textToSpeechOptions = TextToSpeechOptions.builder().build();
    TextToSpeechPrompt textToSpeechPrompt = new TextToSpeechPrompt(text, textToSpeechOptions);
    TextToSpeechResponse textToSpeechResponse = textToSpeechModel.call(textToSpeechPrompt);

    log.info("TextToSpeechResponse: {}", textToSpeechResponse);
    log.info("Metadata: {}", textToSpeechResponse.getMetadata());

    return textToSpeechResponse.getResult().getOutput();
  }
}
