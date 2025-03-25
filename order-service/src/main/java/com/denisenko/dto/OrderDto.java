package com.denisenko.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String status;
    private List<OrderLineItemDto> orderLineItems;
    private Long userId;
    private LocalDateTime deliveryTime;
    private BigDecimal totalPrice;
}
