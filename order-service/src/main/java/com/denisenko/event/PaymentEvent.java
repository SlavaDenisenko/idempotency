package com.denisenko.event;

import com.denisenko.dto.OrderDto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;

    public PaymentEvent(OrderDto orderDto) {
        this.orderId = orderDto.getId();
        this.userId = orderDto.getUserId();
        this.amount = orderDto.getTotalPrice();
    }
}
