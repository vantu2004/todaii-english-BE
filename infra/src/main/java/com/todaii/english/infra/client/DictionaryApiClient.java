package com.todaii.english.infra.client;

import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.shared.constants.ApiUrl;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.DictionaryApiResponse;
import com.todaii.english.shared.response.TodaiiEnglishResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class DictionaryApiClient implements DictionaryPort {
  private final WebClient webClient;

  public DictionaryApiClient(WebClient.Builder builder) {
    this.webClient = builder.build();
  }

  @Override
  public DictionaryApiResponse[] lookupFreeDictionaryApi(String word) {
    String url = ApiUrl.DICTIONARY_BASE_URL + "/" + encode(word);
    return get(url, DictionaryApiResponse[].class);
  }

  @Override
  public TodaiiEnglishResponse lookupTodaiiDictionaryApi(String word, int page, int size) {
    String url = buildTodaiiUrl(word, page, size);
    return get(url, TodaiiEnglishResponse.class);
  }

  // <T> báo là trong hàm này sẽ có sử dụng generic type
  private <T> T get(String url, Class<T> responseType) {
    try {
      return webClient
              .get()
              .uri(url)
              .retrieve()

              // 4xx
              .onStatus(HttpStatusCode::is4xxClientError, res ->
                      res.bodyToMono(String.class)
                              .doOnNext(body -> log.warn("❌ 4xx error: url={} body={}", url, body))
                              .flatMap(body -> Mono.error(new BusinessException(404, "Not found")))
              )

              // 5xx
              .onStatus(HttpStatusCode::is5xxServerError, res ->
                      res.bodyToMono(String.class)
                              .doOnNext(body -> log.error("💥 5xx error: url={} body={}", url, body))
                              .flatMap(body -> Mono.error(new BusinessException(500, body)))
              )

              .bodyToMono(responseType)
              .block();

    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error when calling API: url={} msg={}", url, e.getMessage(), e);
      throw new BusinessException(500, "External API error");
    }
  }

  private String buildTodaiiUrl(String word, int page, int size) {
    return String.format(
            ApiUrl.TODAII_DICT_BASE_URL,
            encode(word),
            page,
            size
    );
  }

  private String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}