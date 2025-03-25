package com.denisenko.kafka;

import com.denisenko.event.InventoryEvent;
import com.denisenko.event.ResultEvent;
import com.denisenko.service.InventoryService;
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
public class KafkaInventoryListener {
    private final InventoryService inventoryService;
    private final TransactionLogService transactionLogService;
    private final KafkaTemplate<String, ResultEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.order-results}")
    private String inventoryResultTopic;

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.reserve-stock}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleReservationRequest(InventoryEvent inventoryEvent) {
        log.info("Received reservation event for order: {}", inventoryEvent.getOrderId());
        boolean isReserved = inventoryService.reserveItems(inventoryEvent.getOrderLineItems());

        ResultEvent resultEvent = ResultEvent.builder()
                .orderId(inventoryEvent.getOrderId())
                .success(isReserved)
                .build();

        transactionLogService.logEvent(inventoryEvent, "RESERVE", isReserved);
        kafkaTemplate.send(inventoryResultTopic, resultEvent);
    }

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.inventory-compensate}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleCompensationRequest(InventoryEvent inventoryEvent) {
        log.info("Received compensation event for order: {}", inventoryEvent.getOrderId());
        boolean isReleased = inventoryService.releaseItems(inventoryEvent.getOrderLineItems());
        transactionLogService.logEvent(inventoryEvent, "RELEASE", isReleased);
    }
}
