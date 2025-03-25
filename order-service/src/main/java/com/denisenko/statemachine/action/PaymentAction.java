package com.denisenko.statemachine.action;

import com.denisenko.dto.OrderDto;
import com.denisenko.event.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.denisenko.statemachine.OrderEvent.PAYMENT;

@Component
@Slf4j
public class PaymentAction extends AbstractAction {

    @Value("${spring.kafka.topics.payment}")
    private String paymentTopic;

    public PaymentAction(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate, PAYMENT);
    }

    @Override
    protected void executeAction(OrderDto orderDto) {
        log.info("Sending message to payment service. Order ID: {}", orderDto.getId());
        kafkaTemplate.send(paymentTopic, new PaymentEvent(orderDto));
    }
}
