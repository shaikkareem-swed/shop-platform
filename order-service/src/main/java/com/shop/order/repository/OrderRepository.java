package com.shop.order.repository;

import java.util.UUID;
import com.shop.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {}
