package com.todaii.english.infra.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.shared.constants.DictionaryApiUrl;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.DictionaryApiResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class DictionaryApiClient implements DictionaryPort {

	private final WebClient webClient;

	public DictionaryApiClient(WebClient.Builder builder) {
		this.webClient = builder.baseUrl(DictionaryApiUrl.BASE_URL_v100).build();
	}

	@Override
	public DictionaryApiResponse[] lookupWord(String word) {
		try {
			return webClient.get()
					.uri("/{word}", word)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> 
						clientResponse.bodyToMono(String.class)
							.doOnNext(body -> log.warn("❌ 4xx error for word '{}': status={} body={}", word, clientResponse.statusCode(), body))
							.flatMap(body -> Mono.error(new BusinessException(404, "Word not found: " + word + " | Response: " + body)))
					)
					.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> 
						clientResponse.bodyToMono(String.class)
							.doOnNext(body -> log.error("💥 5xx error for word '{}': status={} body={}", word, clientResponse.statusCode(), body))
							.flatMap(body -> Mono.error(new BusinessException(500, "Dictionary API error: " + body)))
					)
					.bodyToMono(DictionaryApiResponse[].class)
					.block();

		} catch (BusinessException e) {
			// Đã xử lý trong onStatus, chỉ log thêm nếu cần
			log.warn("BusinessException for word '{}': {}", word, e.getMessage());
			throw e;
		} catch (Exception e) {
			// Các lỗi bất ngờ (timeout, network, parsing,…)
			log.error("⚠️ Unexpected error while looking up '{}': {}", word, e.getMessage(), e);
			throw new BusinessException(500, "Unexpected error while calling dictionary API for: " + word);
		}
	}


}
