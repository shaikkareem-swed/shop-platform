package com.shop.article.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record ArticleRequest(
        @NotNull Long productId,
        @NotBlank @Size(max = 64) String sku,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        @Size(max = 2000) String description) {}
