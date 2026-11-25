package com.shop.order.client;

import java.time.Duration;

import com.shop.common.exception.ResourceNotFoundException;
import com.shop.common.exception.ServiceUnavailableException;
import com.shop.order.dto.ArticleView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Synchronous call to article-service wrapped in a Resilience4j circuit breaker.
 * When article-service is down (or the circuit is open) the fallback raises
 * ServiceUnavailableException, which GlobalExceptionHandler maps to HTTP 503.
 */
@Component
public class ArticleClient {

    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;

    public ArticleClient(@Value("${clients.article.url}") String baseUrl, CircuitBreakerFactory<?, ?> factory) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(2));
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
        this.circuitBreaker = factory.create("articleService");
    }

    public ArticleView getArticle(Long articleId) {
        return circuitBreaker.run(
                () -> restClient.get().uri("/api/articles/{id}", articleId)
                        .retrieve()
                        .onStatus(status -> status.value() == 404, (req, res) -> {
                            throw new ResourceNotFoundException("Article", articleId);
                        })
                        .body(ArticleView.class),
                throwable -> {
                    if (throwable instanceof ResourceNotFoundException notFound) throw notFound;
                    throw new ServiceUnavailableException(
                            "article-service is currently unavailable, please retry later", throwable);
                });
    }
}
