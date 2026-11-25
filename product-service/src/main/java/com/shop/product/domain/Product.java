package com.shop.product.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false, length = 80)
    private String category;
    @Column(length = 2000)
    private String description;

    protected Product() {}

    public Product(String name, String category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public void update(String name, String category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }
}
