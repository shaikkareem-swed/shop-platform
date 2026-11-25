package com.shop.product.dto;

import java.io.Serializable;
import com.shop.product.domain.Product;

/** Serializable because it is stored in the Redis cache. */
public record ProductResponse(Long id, String name, String category, String description) implements Serializable {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getCategory(), p.getDescription());
    }
}
