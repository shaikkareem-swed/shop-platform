package com.shop.order.web;

import java.util.List;
import java.util.UUID;

import com.shop.order.dto.OrderResponse;
import com.shop.order.query.OrderQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderQueryController {

    private final OrderQueryService queries;

    public OrderQueryController(OrderQueryService queries) {
        this.queries = queries;
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable UUID id) { return queries.get(id); }

    @GetMapping
    public List<OrderResponse> listByCustomer(@RequestParam String customerId) {
        return queries.listByCustomer(customerId);
    }
}
