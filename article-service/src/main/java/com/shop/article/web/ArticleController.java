package com.shop.article.web;

import java.net.URI;
import java.util.List;

import com.shop.article.dto.ArticleRequest;
import com.shop.article.dto.ArticleResponse;
import com.shop.article.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService service;

    public ArticleController(ArticleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> create(@Valid @RequestBody ArticleRequest request) {
        ArticleResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/articles/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public ArticleResponse get(@PathVariable Long id) { return service.get(id); }

    @GetMapping
    public List<ArticleResponse> list(@RequestParam(required = false) Long productId) { return service.list(productId); }

    @PutMapping("/{id}")
    public ArticleResponse update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
