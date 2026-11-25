package com.shop.inventory.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    private Long articleId;
    @Column(nullable = false)
    private int available;
    @Column(nullable = false)
    private int reserved;

    protected Inventory() {}

    public Inventory(Long articleId) { this.articleId = articleId; }

    public Long getArticleId() { return articleId; }
    public int getAvailable() { return available; }
    public int getReserved() { return reserved; }
    public void setAvailable(int available) { this.available = available; }

    public void reserve(int qty) {
        this.available -= qty;
        this.reserved += qty;
    }
}
