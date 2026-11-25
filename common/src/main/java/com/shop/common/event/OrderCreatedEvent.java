package com.shop.common.event;

import java.util.UUID;

public record OrderCreatedEvent(UUID orderId, Long articleId, int quantity) {}
