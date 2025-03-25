package com.denisenko.repository;

import com.denisenko.model.Order;
import com.denisenko.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
    @Transactional
    void updateOrderStatusById(@Param("id") Long orderId, @Param("status") OrderStatus status);
}
