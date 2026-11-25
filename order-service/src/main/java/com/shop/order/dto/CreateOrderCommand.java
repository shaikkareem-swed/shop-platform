package com.shop.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderCommand(@NotBlank String customerId, @NotNull Long articleId, @Min(1) int quantity) {}
