package com.todaii.english.infra.client;

import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import com.todaii.english.core.port.NewsApiPort;
import com.todaii.english.shared.response.NewsApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// phải đổi tên để tránh trùng tên class trong thư viện
@Slf4j
@Component
public class NewsApiClientImpl implements NewsApiPort {

    private final NewsApiClient client;
    private final ModelMapper modelMapper;

    public NewsApiClientImpl(@Value("${newsapi.api.key}") String apiKey, ModelMapper modelMapper) {
        this.client = new NewsApiClient(apiKey);
        this.modelMapper = modelMapper;
    }

    @Override
    public NewsApiResponse fetchFromNewsApi(String query, int pageSize, int page, String sortBy) {
        CompletableFuture<NewsApiResponse> future = new CompletableFuture<>();

        client.getEverything(
                new EverythingRequest.Builder()
                        .q(query)
                        .language("en")
                        .pageSize(pageSize)
                        .page(page)
                        .sortBy(sortBy)
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        NewsApiResponse mapped = new NewsApiResponse();
                        mapped.setStatus(response.getStatus());
                        mapped.setTotalResults(response.getTotalResults());

                        // Dùng ModelMapper để chuyển list Article → list ArticleData
                        Type listType = new TypeToken<List<NewsApiResponse.ArticleData>>() {}.getType();
                        List<NewsApiResponse.ArticleData> mappedArticles =
                                modelMapper.map(response.getArticles(), listType);

                        mapped.setArticles(mappedArticles);
                        future.complete(mapped);
                        
                        log.info(response.getArticles().get(0).getPublishedAt());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        log.error("NewsAPI fetch failed: {}", throwable.getMessage());
                        future.completeExceptionally(throwable);
                    }
                });

        return future.join();
    }
}
