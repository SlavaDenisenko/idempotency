package com.denisenko.kafka;

import com.denisenko.event.DeliveryEvent;
import com.denisenko.event.ResultEvent;
import com.denisenko.service.DeliveryService;
import com.denisenko.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaDeliveryListener {
    private final DeliveryService deliveryService;
    private final TransactionLogService transactionLogService;
    private final KafkaTemplate<String, ResultEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.order-results}")
    private String deliveryResultTopic;

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.book-delivery}", groupId = "${spring.kafka.consumer.group-id")
    public void handleReserveDelivery(DeliveryEvent deliveryEvent) {
        log.info("Received courier reservation event for order: {}. Delivery time: {}", deliveryEvent.getOrderId(), deliveryEvent.getDeliveryTime());
        boolean isReserved = deliveryService.reserveSlot(deliveryEvent.getOrderId(), deliveryEvent.getDeliveryTime());

        ResultEvent resultEvent = ResultEvent.builder()
                .orderId(deliveryEvent.getOrderId())
                .success(isReserved)
                .build();

        transactionLogService.logEvent(deliveryEvent, "RESERVE", isReserved);
        kafkaTemplate.send(deliveryResultTopic, resultEvent);
    }

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.delivery-compensate}", groupId = "${spring.kafka.consumer.group-id")
    public void handleCancelDelivery(DeliveryEvent deliveryEvent) {
        log.info("Received compensation event for order: {}", deliveryEvent.getOrderId());
        boolean isReleased = deliveryService.cancelReservation(deliveryEvent.getOrderId());
        transactionLogService.logEvent(deliveryEvent, "RELEASE", isReleased);
    }
}
