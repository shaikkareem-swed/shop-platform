package com.shop.article.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import com.shop.article.domain.Article;

public record ArticleResponse(Long id, Long productId, String sku, BigDecimal price, String description)
        implements Serializable {
    public static ArticleResponse from(Article a) {
        return new ArticleResponse(a.getId(), a.getProductId(), a.getSku(), a.getPrice(), a.getDescription());
    }
}
