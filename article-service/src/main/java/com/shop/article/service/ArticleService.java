package com.shop.article.service;

import java.util.List;

import com.shop.article.domain.Article;
import com.shop.article.dto.ArticleRequest;
import com.shop.article.dto.ArticleResponse;
import com.shop.article.repository.ArticleRepository;
import com.shop.common.exception.BusinessException;
import com.shop.common.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {

    private final ArticleRepository repo;

    public ArticleService(ArticleRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public ArticleResponse create(ArticleRequest r) {
        if (repo.existsBySku(r.sku())) throw new BusinessException("SKU already exists: " + r.sku());
        return ArticleResponse.from(repo.save(new Article(r.productId(), r.sku(), r.price(), r.description())));
    }

    @Cacheable(cacheNames = "articles", key = "#id")
    @Transactional(readOnly = true)
    public ArticleResponse get(Long id) {
        return repo.findById(id).map(ArticleResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> list(Long productId) {
        List<Article> items = productId == null ? repo.findAll() : repo.findByProductId(productId);
        return items.stream().map(ArticleResponse::from).toList();
    }

    @CachePut(cacheNames = "articles", key = "#id")
    @Transactional
    public ArticleResponse update(Long id, ArticleRequest r) {
        Article a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Article", id));
        if (repo.existsBySkuAndIdNot(r.sku(), id)) throw new BusinessException("SKU already exists: " + r.sku());
        a.update(r.productId(), r.sku(), r.price(), r.description());
        return ArticleResponse.from(a);
    }

    @CacheEvict(cacheNames = "articles", key = "#id")
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Article", id);
        repo.deleteById(id);
    }
}
