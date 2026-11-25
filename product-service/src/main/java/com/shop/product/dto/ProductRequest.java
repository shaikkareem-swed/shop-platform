package com.shop.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 80) String category,
        @Size(max = 2000) String description) {}
