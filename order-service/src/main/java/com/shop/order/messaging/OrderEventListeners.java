package com.shop.order.messaging;

import com.shop.common.event.InventoryRejectedEvent;
import com.shop.common.event.InventoryReservedEvent;
import com.shop.common.event.OrderStateEvent;
import com.shop.common.event.Topics;
import com.shop.common.util.Json;
import com.shop.order.command.OrderCommandService;
import com.shop.order.query.OrderProjection;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListeners {

    private final OrderCommandService commands;
    private final OrderProjection projection;

    public OrderEventListeners(OrderCommandService commands, OrderProjection projection) {
        this.commands = commands;
        this.projection = projection;
    }

    // ---- Saga: replies from inventory-service ----
    @KafkaListener(topics = Topics.INVENTORY_RESERVED, groupId = "order-saga")
    public void onReserved(String payload) {
        commands.confirm(Json.read(payload, InventoryReservedEvent.class).orderId());
    }

    @KafkaListener(topics = Topics.INVENTORY_REJECTED, groupId = "order-saga")
    public void onRejected(String payload) {
        InventoryRejectedEvent e = Json.read(payload, InventoryRejectedEvent.class);
        commands.cancel(e.orderId(), e.reason());          // compensating transaction
    }

    // ---- CQRS: build the read model ----
    @KafkaListener(topics = Topics.ORDER_EVENTS, groupId = "order-projection")
    public void onOrderState(String payload) {
        projection.apply(Json.read(payload, OrderStateEvent.class));
    }
}
