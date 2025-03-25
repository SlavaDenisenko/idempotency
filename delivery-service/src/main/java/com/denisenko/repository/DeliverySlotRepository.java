package com.denisenko.repository;

import com.denisenko.model.DeliverySlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DeliverySlotRepository extends JpaRepository<DeliverySlot, Long> {

    Optional<DeliverySlot> findFirstByDeliveryTimeAndReservedFalse(LocalDateTime deliveryTime);

    Optional<DeliverySlot> findFirstByOrderId(Long orderId);
}
