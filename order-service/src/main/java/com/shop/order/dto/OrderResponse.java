package com.shop.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.shop.order.domain.OrderView;

public record OrderResponse(UUID id, String customerId, Long articleId, int quantity, BigDecimal totalPrice,
                            String status, String cancelReason, Instant createdAt, Instant updatedAt)
        implements Serializable {
    public static OrderResponse from(OrderView v) {
        return new OrderResponse(v.getId(), v.getCustomerId(), v.getArticleId(), v.getQuantity(),
                v.getTotalPrice(), v.getStatus(), v.getCancelReason(), v.getCreatedAt(), v.getUpdatedAt());
    }
}
