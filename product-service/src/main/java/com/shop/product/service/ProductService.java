package com.shop.product.service;

import java.util.List;

import com.shop.common.exception.ResourceNotFoundException;
import com.shop.product.domain.Product;
import com.shop.product.dto.ProductRequest;
import com.shop.product.dto.ProductResponse;
import com.shop.product.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public ProductResponse create(ProductRequest r) {
        return ProductResponse.from(repo.save(new Product(r.name(), r.category(), r.description())));
    }

    @Cacheable(cacheNames = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return repo.findById(id).map(ProductResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return repo.findAll().stream().map(ProductResponse::from).toList();
    }

    @CachePut(cacheNames = "products", key = "#id")
    @Transactional
    public ProductResponse update(Long id, ProductRequest r) {
        Product p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        p.update(r.name(), r.category(), r.description());
        return ProductResponse.from(p);
    }

    @CacheEvict(cacheNames = "products", key = "#id")
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Product", id);
        repo.deleteById(id);
    }
}
