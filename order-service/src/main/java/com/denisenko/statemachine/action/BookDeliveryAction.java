package com.denisenko.statemachine.action;

import com.denisenko.dto.OrderDto;
import com.denisenko.event.DeliveryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.denisenko.statemachine.OrderEvent.BOOK_DELIVERY;

@Component
@Slf4j
public class BookDeliveryAction extends AbstractAction {

    @Value("${spring.kafka.topics.book-delivery}")
    private String bookDeliveryTopic;

    public BookDeliveryAction(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate, BOOK_DELIVERY);
    }

    @Override
    protected void executeAction(OrderDto orderDto) {
        log.info("Sending message to delivery service. Order ID: {}", orderDto.getId());
        kafkaTemplate.send(bookDeliveryTopic, new DeliveryEvent(orderDto));
    }
}
