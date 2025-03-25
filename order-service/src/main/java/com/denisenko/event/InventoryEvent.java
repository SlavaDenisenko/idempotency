package com.denisenko.event;

import com.denisenko.dto.OrderDto;
import com.denisenko.dto.OrderLineItemDto;
import lombok.Data;

import java.util.List;

@Data
public class InventoryEvent {
    private Long orderId;
    private List<OrderLineItemDto> orderLineItems;

    public InventoryEvent(OrderDto orderDto) {
        this.orderId = orderDto.getId();
        this.orderLineItems = orderDto.getOrderLineItems();
    }
}
