package com.todaii.english.infra.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.shared.constants.ApiUrl;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.DictionaryApiResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DictionaryApiClient implements DictionaryPort {

	private final WebClient webClient;

	public DictionaryApiClient(WebClient.Builder builder) {
		this.webClient = builder.baseUrl(ApiUrl.DICTIONARY_BASE_URL).build();
	}

	@Override
	public DictionaryApiResponse[] lookupWord(String word) {
		try {
			// Gửi request GET tới API với path parameter là từ cần tra
			return webClient.get().uri("/{word}", word).retrieve()

					// Xử lý lỗi 4xx (client-side)
					.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse
							.bodyToMono(String.class)
							// Log chi tiết lỗi từ server (nếu có body trả về)
							.doOnNext(body -> log.warn("❌ 4xx error for word '{}': status={} body={}", word,
									clientResponse.statusCode(), body))
							// Ném BusinessException với thông tin cụ thể
							.flatMap(body -> Mono.error(
									new BusinessException(404, "Word not found: " + word + " | Response: " + body))))

					// Xử lý lỗi 5xx (server-side)
					.onStatus(HttpStatusCode::is5xxServerError,
							clientResponse -> clientResponse.bodyToMono(String.class)
									// Log lỗi mức độ nghiêm trọng hơn
									.doOnNext(body -> log.error("💥 5xx error for word '{}': status={} body={}", word,
											clientResponse.statusCode(), body))
									// Ném BusinessException để controller phía trên xử lý
									.flatMap(body -> Mono
											.error(new BusinessException(500, "Dictionary API error: " + body))))

					// Parse body JSON → mảng DictionaryApiResponse
					.bodyToMono(DictionaryApiResponse[].class)

					// Chuyển từ reactive (Mono) sang blocking để trả về mảng kết quả
					.block();

		} catch (BusinessException e) {
			// Bắt lại lỗi đã xử lý trong onStatus, chỉ log thêm cho dễ debug
			log.warn("BusinessException for word '{}': {}", word, e.getMessage());
			throw e;
		} catch (Exception e) {
			// Bắt các lỗi không mong muốn khác: timeout, network, parsing,...
			log.error("Unexpected error while looking up '{}': {}", word, e.getMessage(), e);
			throw new BusinessException(500, "Unexpected error while calling dictionary API for: " + word);
		}
	}

}
