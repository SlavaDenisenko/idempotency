package com.denisenko.kafka;

import com.denisenko.event.PaymentEvent;
import com.denisenko.event.ResultEvent;
import com.denisenko.service.PaymentService;
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
public class KafkaPaymentListener {
    private final PaymentService paymentService;
    private final TransactionLogService transactionLogService;
    private final KafkaTemplate<String, ResultEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.order-results}")
    private String paymentResultTopic;

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.payment}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleProcessPayment(PaymentEvent paymentEvent) {
        log.info("Received payment event for order: {}", paymentEvent.getOrderId());
        boolean isPaid = paymentService.processPayment(paymentEvent.getUserId(), paymentEvent.getAmount());

        ResultEvent resultEvent = ResultEvent.builder()
                .orderId(paymentEvent.getOrderId())
                .success(isPaid)
                .build();

        transactionLogService.logEvent(paymentEvent, "PROCESS PAYMENT", isPaid);
        kafkaTemplate.send(paymentResultTopic, resultEvent);
    }
}
