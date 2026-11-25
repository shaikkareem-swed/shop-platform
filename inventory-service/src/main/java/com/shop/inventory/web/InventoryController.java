package com.shop.inventory.web;

import com.shop.inventory.dto.InventoryResponse;
import com.shop.inventory.dto.StockRequest;
import com.shop.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PutMapping("/{articleId}")
    public InventoryResponse setStock(@PathVariable Long articleId, @Valid @RequestBody StockRequest request) {
        return service.setStock(articleId, request.quantity());
    }

    @GetMapping("/{articleId}")
    public InventoryResponse get(@PathVariable Long articleId) {
        return service.get(articleId);
    }
}
