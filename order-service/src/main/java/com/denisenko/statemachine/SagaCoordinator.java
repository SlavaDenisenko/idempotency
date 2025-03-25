package com.denisenko.statemachine;

import com.denisenko.dto.OrderDto;
import com.denisenko.event.ResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaCoordinator {
    private final SagaOrchestrator sagaOrchestrator;

    @EventListener
    public void startOrderSaga(OrderDto orderDto) {
        log.info("Starting order saga... Order ID: {}", orderDto.getId());
        sagaOrchestrator.startOrderSaga(orderDto);
    }

    @KafkaListener(topics = "${spring.kafka.topics.order-results}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleServiceResponse(ResultEvent resultEvent) {
        log.info("Received answer. Order ID: {}, success: {}", resultEvent.getOrderId(), resultEvent.getSuccess());
        sagaOrchestrator.handleResult(resultEvent);
    }
}
