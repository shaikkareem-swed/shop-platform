package com.shop.order.dto;

import java.math.BigDecimal;

/** Subset of article-service's response that order-service cares about. */
public record ArticleView(Long id, Long productId, String sku, BigDecimal price) {}
