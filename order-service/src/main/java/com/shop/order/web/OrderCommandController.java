package com.shop.order.web;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import com.shop.order.command.OrderCommandService;
import com.shop.order.dto.CreateOrderCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderCommandController {

    private final OrderCommandService commands;

    public OrderCommandController(OrderCommandService commands) {
        this.commands = commands;
    }

    /** 202 Accepted: the order is PENDING until the saga finishes; poll GET /api/orders/{id}. */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateOrderCommand cmd) {
        UUID id = commands.createOrder(cmd);
        return ResponseEntity.accepted().location(URI.create("/api/orders/" + id))
                .body(Map.of("orderId", id, "status", "PENDING"));
    }
}
