package com.shop.common.event;

import java.util.UUID;

public record InventoryRejectedEvent(UUID orderId, String reason) {}
