package com.shop.inventory.service;

import com.shop.common.exception.ResourceNotFoundException;
import com.shop.inventory.domain.Inventory;
import com.shop.inventory.dto.InventoryResponse;
import com.shop.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository repo;

    public InventoryService(InventoryRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public InventoryResponse setStock(Long articleId, int quantity) {
        Inventory inv = repo.findForUpdate(articleId).orElseGet(() -> new Inventory(articleId));
        inv.setAvailable(quantity);
        return InventoryResponse.from(repo.save(inv));
    }

    @Transactional(readOnly = true)
    public InventoryResponse get(Long articleId) {
        return repo.findById(articleId).map(InventoryResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for article", articleId));
    }
}
