package com.denisenko.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverySlotDto {
    private Long courierId;
    private LocalDateTime deliveryTime;
    private boolean reserved;
    private Long orderId;
}
