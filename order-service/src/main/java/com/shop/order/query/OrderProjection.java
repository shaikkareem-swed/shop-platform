package com.shop.order.query;

import com.shop.common.event.OrderStateEvent;
import com.shop.order.domain.OrderView;
import com.shop.order.repository.OrderViewRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Keeps the read model in sync with order-events and evicts the stale Redis entry. */
@Service
public class OrderProjection {

    private final OrderViewRepository views;

    public OrderProjection(OrderViewRepository views) {
        this.views = views;
    }

    @Transactional
    @CacheEvict(cacheNames = "orders", key = "#event.orderId().toString()")
    public void apply(OrderStateEvent event) {
        views.findById(event.orderId()).ifPresentOrElse(view -> {
            if (!event.updatedAt().isBefore(view.getUpdatedAt())) view.apply(event);
        }, () -> views.save(OrderView.from(event)));
    }
}
