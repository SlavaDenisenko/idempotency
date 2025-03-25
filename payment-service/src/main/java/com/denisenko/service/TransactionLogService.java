package com.denisenko.service;

import com.denisenko.event.PaymentEvent;
import com.denisenko.model.TransactionLog;
import com.denisenko.repository.TransactionLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionLogService {
    private final TransactionLogRepository transactionLogRepository;

    public void logEvent(PaymentEvent paymentEvent, String eventType, boolean success) {
        TransactionLog transactionLog = TransactionLog.builder()
                .orderId(paymentEvent.getOrderId())
                .eventType(eventType)
                .payload(toJson(paymentEvent))
                .result(success ? "SUCCESS" : "FAILURE")
                .build();

        transactionLogRepository.save(transactionLog);
    }

    private String toJson(PaymentEvent paymentEvent) {
        try {
            return new ObjectMapper().writeValueAsString(paymentEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event to JSON", e);
        }
    }
}
