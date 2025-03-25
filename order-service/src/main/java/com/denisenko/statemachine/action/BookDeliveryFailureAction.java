package com.denisenko.statemachine.action;

import com.denisenko.dto.OrderDto;
import com.denisenko.event.InventoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.denisenko.statemachine.OrderEvent.BOOK_DELIVERY_FAILURE;

@Component
@Slf4j
public class BookDeliveryFailureAction extends AbstractAction {

    @Value("${spring.kafka.topics.inventory-compensate}")
    private String cancelStockReservationTopic;

    public BookDeliveryFailureAction(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate, BOOK_DELIVERY_FAILURE);
    }

    @Override
    protected void executeAction(OrderDto orderDto) {
        log.warn("Delivery wasn't booked. Order ID: {}", orderDto.getId());
        kafkaTemplate.send(cancelStockReservationTopic, new InventoryEvent(orderDto));
    }
}
