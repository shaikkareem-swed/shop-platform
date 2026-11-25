package com.shop.inventory.repository;

import java.util.UUID;
import com.shop.inventory.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {}
