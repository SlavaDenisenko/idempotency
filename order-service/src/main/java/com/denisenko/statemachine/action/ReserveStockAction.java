package com.denisenko.statemachine.action;

import com.denisenko.dto.OrderDto;
import com.denisenko.event.InventoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.denisenko.statemachine.OrderEvent.RESERVE_STOCK;

@Component
@Slf4j
public class ReserveStockAction extends AbstractAction {

    @Value("${spring.kafka.topics.reserve-stock}")
    private String reserveStockTopic;

    public ReserveStockAction(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate, RESERVE_STOCK);
    }

    @Override
    protected void executeAction(OrderDto orderDto) {
        log.info("Sending message to inventory service. Order ID: {}", orderDto.getId());
        kafkaTemplate.send(reserveStockTopic, new InventoryEvent(orderDto));
    }
}
