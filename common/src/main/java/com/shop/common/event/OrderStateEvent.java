package com.shop.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderStateEvent(UUID orderId, String customerId, Long articleId, int quantity,
                              BigDecimal totalPrice, String status, String cancelReason,
                              Instant createdAt, Instant updatedAt) {}
