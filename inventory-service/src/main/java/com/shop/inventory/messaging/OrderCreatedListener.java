package com.shop.inventory.messaging;

import com.shop.common.event.OrderCreatedEvent;
import com.shop.common.event.Topics;
import com.shop.common.util.Json;
import com.shop.inventory.service.ReservationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    private final ReservationService reservationService;

    public OrderCreatedListener(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @KafkaListener(topics = Topics.ORDER_CREATED, groupId = "inventory-service")
    public void onOrderCreated(String payload) {
        reservationService.reserve(Json.read(payload, OrderCreatedEvent.class));
    }
}
