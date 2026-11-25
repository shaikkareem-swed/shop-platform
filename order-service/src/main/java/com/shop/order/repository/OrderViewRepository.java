package com.shop.order.repository;

import java.util.List;
import java.util.UUID;
import com.shop.order.domain.OrderView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderViewRepository extends JpaRepository<OrderView, UUID> {
    List<OrderView> findByCustomerIdOrderByCreatedAtDesc(String customerId);
}
