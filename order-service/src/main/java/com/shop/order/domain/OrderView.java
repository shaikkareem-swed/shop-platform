package com.shop.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.shop.common.event.OrderStateEvent;
import jakarta.persistence.*;

/** READ model (CQRS query side) - built only from Kafka order-events by OrderProjection. */
@Entity
@Table(name = "order_view")
public class OrderView {
    @Id
    private UUID id;
    private String customerId;
    private Long articleId;
    private int quantity;
    private BigDecimal totalPrice;
    private String status;
    private String cancelReason;
    private Instant createdAt;
    private Instant updatedAt;

    protected OrderView() {}

    public static OrderView from(OrderStateEvent e) {
        OrderView v = new OrderView();
        v.id = e.orderId();
        v.apply(e);
        return v;
    }

    public void apply(OrderStateEvent e) {
        this.customerId = e.customerId();
        this.articleId = e.articleId();
        this.quantity = e.quantity();
        this.totalPrice = e.totalPrice();
        this.status = e.status();
        this.cancelReason = e.cancelReason();
        this.createdAt = e.createdAt();
        this.updatedAt = e.updatedAt();
    }

    public UUID getId() { return id; }
    public String getCustomerId() { return customerId; }
    public Long getArticleId() { return articleId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getCancelReason() { return cancelReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
