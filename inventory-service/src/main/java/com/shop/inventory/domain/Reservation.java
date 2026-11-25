package com.shop.inventory.domain;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;

/** One row per order -> makes the saga step idempotent (Kafka delivers at-least-once). */
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    private UUID orderId;
    @Column(nullable = false)
    private Long articleId;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Reservation() {}

    public Reservation(UUID orderId, Long articleId, int quantity) {
        this.orderId = orderId;
        this.articleId = articleId;
        this.quantity = quantity;
    }

    public UUID getOrderId() { return orderId; }
}
