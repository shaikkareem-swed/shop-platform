package com.shop.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;

/** WRITE model (CQRS command side). */
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String customerId;
    @Column(nullable = false)
    private Long articleId;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;
    private String cancelReason;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    protected Order() {}

    public Order(String customerId, Long articleId, int quantity, BigDecimal totalPrice) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.articleId = articleId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    /** Compensating action of the saga. */
    public void cancel(String reason) {
        this.status = OrderStatus.CANCELLED;
        this.cancelReason = reason;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getCustomerId() { return customerId; }
    public Long getArticleId() { return articleId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public OrderStatus getStatus() { return status; }
    public String getCancelReason() { return cancelReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
