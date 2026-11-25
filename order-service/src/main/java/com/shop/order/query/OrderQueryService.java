package com.shop.order.query;

import java.util.List;
import java.util.UUID;

import com.shop.common.exception.ResourceNotFoundException;
import com.shop.order.dto.OrderResponse;
import com.shop.order.repository.OrderViewRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CQRS query side: reads only from the read model, with a Redis cache in front. */
@Service
public class OrderQueryService {

    private final OrderViewRepository views;

    public OrderQueryService(OrderViewRepository views) {
        this.views = views;
    }

    @Cacheable(cacheNames = "orders", key = "#id.toString()")
    @Transactional(readOnly = true)
    public OrderResponse get(UUID id) {
        return views.findById(id).map(OrderResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listByCustomer(String customerId) {
        return views.findByCustomerIdOrderByCreatedAtDesc(customerId).stream().map(OrderResponse::from).toList();
    }
}
