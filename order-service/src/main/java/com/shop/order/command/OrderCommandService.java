package com.shop.order.command;

import java.math.BigDecimal;
import java.util.UUID;

import com.shop.common.event.OrderCreatedEvent;
import com.shop.common.event.OrderStateEvent;
import com.shop.common.event.Topics;
import com.shop.order.client.ArticleClient;
import com.shop.order.domain.Order;
import com.shop.order.domain.OrderStatus;
import com.shop.order.dto.ArticleView;
import com.shop.order.dto.CreateOrderCommand;
import com.shop.order.messaging.EventPublisher;
import com.shop.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CQRS command side + saga orchestration point (choreography style):
 *   createOrder -> PENDING -> [inventory-service reserves] -> confirm() or cancel() (compensation)
 * Every state change is also published to order-events so the read model can be updated.
 * (Production hardening: use the transactional-outbox pattern instead of sending inside the transaction.)
 */
@Service
public class OrderCommandService {

    private static final Logger log = LoggerFactory.getLogger(OrderCommandService.class);

    private final OrderRepository orders;
    private final ArticleClient articleClient;
    private final EventPublisher publisher;

    public OrderCommandService(OrderRepository orders, ArticleClient articleClient, EventPublisher publisher) {
        this.orders = orders;
        this.articleClient = articleClient;
        this.publisher = publisher;
    }

    @Transactional
    public UUID createOrder(CreateOrderCommand cmd) {
        ArticleView article = articleClient.getArticle(cmd.articleId());   // circuit-breaker protected
        BigDecimal total = article.price().multiply(BigDecimal.valueOf(cmd.quantity()));

        Order order = orders.save(new Order(cmd.customerId(), cmd.articleId(), cmd.quantity(), total));
        String key = order.getId().toString();
        publisher.send(Topics.ORDER_CREATED, key, new OrderCreatedEvent(order.getId(), order.getArticleId(), order.getQuantity()));
        publishState(order);
        return order.getId();
    }

    @Transactional
    public void confirm(UUID orderId) {
        orders.findById(orderId).ifPresent(order -> {
            if (order.getStatus() != OrderStatus.PENDING) return;      // idempotent
            order.confirm();
            publishState(order);
            log.info("Order {} CONFIRMED", orderId);
        });
    }

    @Transactional
    public void cancel(UUID orderId, String reason) {
        orders.findById(orderId).ifPresent(order -> {
            if (order.getStatus() != OrderStatus.PENDING) return;      // idempotent
            order.cancel(reason);
            publishState(order);
            log.info("Order {} CANCELLED: {}", orderId, reason);
        });
    }

    private void publishState(Order o) {
        publisher.send(Topics.ORDER_EVENTS, o.getId().toString(), new OrderStateEvent(o.getId(), o.getCustomerId(),
                o.getArticleId(), o.getQuantity(), o.getTotalPrice(), o.getStatus().name(), o.getCancelReason(),
                o.getCreatedAt(), o.getUpdatedAt()));
    }
}
