package com.denisenko.statemachine.action;

import com.denisenko.dto.OrderDto;
import com.denisenko.event.DeliveryEvent;
import com.denisenko.event.InventoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.denisenko.statemachine.OrderEvent.PAYMENT_FAILURE;

@Component
@Slf4j
public class PaymentFailureAction extends AbstractAction {

    @Value("${spring.kafka.topics.inventory-compensate}")
    private String cancelStockReservationTopic;

    @Value("${spring.kafka.topics.delivery-compensate}")
    private String cancelDeliveryBookingTopic;

    public PaymentFailureAction(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate, PAYMENT_FAILURE);
    }

    @Override
    protected void executeAction(OrderDto orderDto) {
        log.warn("Payment was failed. Order ID: {}", orderDto.getId());
        kafkaTemplate.send(cancelStockReservationTopic, new InventoryEvent(orderDto));
        kafkaTemplate.send(cancelDeliveryBookingTopic, new DeliveryEvent(orderDto));
    }
}
