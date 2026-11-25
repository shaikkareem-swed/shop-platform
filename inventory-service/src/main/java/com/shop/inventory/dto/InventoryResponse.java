package com.shop.inventory.dto;

import com.shop.inventory.domain.Inventory;

public record InventoryResponse(Long articleId, int available, int reserved) {
    public static InventoryResponse from(Inventory i) {
        return new InventoryResponse(i.getArticleId(), i.getAvailable(), i.getReserved());
    }
}
