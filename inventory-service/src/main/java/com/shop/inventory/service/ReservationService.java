package com.shop.inventory.service;

import com.shop.common.event.InventoryRejectedEvent;
import com.shop.common.event.InventoryReservedEvent;
import com.shop.common.event.OrderCreatedEvent;
import com.shop.common.event.Topics;
import com.shop.inventory.domain.Inventory;
import com.shop.inventory.domain.Reservation;
import com.shop.inventory.messaging.EventPublisher;
import com.shop.inventory.repository.InventoryRepository;
import com.shop.inventory.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Saga participant: reserve stock for an order, answer with reserved / rejected. */
@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final InventoryRepository inventories;
    private final ReservationRepository reservations;
    private final EventPublisher publisher;

    public ReservationService(InventoryRepository inventories, ReservationRepository reservations,
                              EventPublisher publisher) {
        this.inventories = inventories;
        this.reservations = reservations;
        this.publisher = publisher;
    }

    @Transactional
    public void reserve(OrderCreatedEvent e) {
        String key = e.orderId().toString();
        if (reservations.existsById(e.orderId())) {
            log.info("Order {} already processed, re-sending reserved event", key);
            publisher.send(Topics.INVENTORY_RESERVED, key, new InventoryReservedEvent(e.orderId()));
            return;
        }
        Inventory inv = inventories.findForUpdate(e.articleId()).orElse(null);
        if (inv == null) {
            reject(e, "No stock record for article " + e.articleId());
        } else if (inv.getAvailable() < e.quantity()) {
            reject(e, "Insufficient stock for article " + e.articleId()
                    + " (available=" + inv.getAvailable() + ", requested=" + e.quantity() + ")");
        } else {
            inv.reserve(e.quantity());
            reservations.save(new Reservation(e.orderId(), e.articleId(), e.quantity()));
            publisher.send(Topics.INVENTORY_RESERVED, key, new InventoryReservedEvent(e.orderId()));
            log.info("Reserved {} x article {} for order {}", e.quantity(), e.articleId(), key);
        }
    }

    private void reject(OrderCreatedEvent e, String reason) {
        log.info("Rejecting order {}: {}", e.orderId(), reason);
        publisher.send(Topics.INVENTORY_REJECTED, e.orderId().toString(), new InventoryRejectedEvent(e.orderId(), reason));
    }
}
