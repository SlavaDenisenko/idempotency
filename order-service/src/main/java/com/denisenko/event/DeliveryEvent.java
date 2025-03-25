package com.denisenko.event;

import com.denisenko.dto.OrderDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryEvent {
    private Long orderId;
    private LocalDateTime deliveryTime;

    public DeliveryEvent(OrderDto orderDto) {
        this.orderId = orderDto.getId();
        this.deliveryTime = orderDto.getDeliveryTime();
    }
}
