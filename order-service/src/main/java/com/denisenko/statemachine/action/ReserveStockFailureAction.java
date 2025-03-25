package com.denisenko.statemachine.action;

import com.denisenko.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.denisenko.statemachine.OrderEvent.RESERVE_STOCK_FAILURE;

@Component
@Slf4j
public class ReserveStockFailureAction extends AbstractAction {

    public ReserveStockFailureAction(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate, RESERVE_STOCK_FAILURE);
    }

    @Override
    protected void executeAction(OrderDto orderDto) {
        log.warn("Stock wasn't reserved. Order ID: {}", orderDto.getId());
    }
}
