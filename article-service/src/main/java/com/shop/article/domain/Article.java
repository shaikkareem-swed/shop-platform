package com.shop.article.domain;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long productId;
    @Column(nullable = false, unique = true, length = 64)
    private String sku;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    @Column(length = 2000)
    private String description;

    protected Article() {}

    public Article(Long productId, String sku, BigDecimal price, String description) {
        this.productId = productId;
        this.sku = sku;
        this.price = price;
        this.description = description;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getSku() { return sku; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }
    public void update(Long productId, String sku, BigDecimal price, String description) {
        this.productId = productId;
        this.sku = sku;
        this.price = price;
        this.description = description;
    }
}
