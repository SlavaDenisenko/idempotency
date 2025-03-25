package com.denisenko.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
}
