package com.todaii.english.infra.dictionary;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.shared.constants.DictionaryApiUrl;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.DictionaryApiResponse;

import reactor.core.publisher.Mono;

@Component
public class DictionaryApiClient implements DictionaryPort {

	private final WebClient webClient;

	public DictionaryApiClient(WebClient.Builder builder) {
		this.webClient = builder.baseUrl(DictionaryApiUrl.BASE_URL).build();
	}

	@Override
	public DictionaryApiResponse[] lookupWord(String word) {
		return webClient.get().uri("/{word}", word).retrieve()
				.onStatus(HttpStatusCode::is4xxClientError,
						clientResponse -> Mono.error(new BusinessException(404, "Word not found: " + word)))
				.onStatus(HttpStatusCode::is5xxServerError,
						clientResponse -> Mono.error(new BusinessException(500, "Dictionary API error")))
				.bodyToMono(DictionaryApiResponse[].class).block();
	}

}
