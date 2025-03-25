package com.denisenko.service;

import com.denisenko.event.DeliveryEvent;
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
    private final ObjectMapper objectMapper;

    public void logEvent(DeliveryEvent deliveryEvent, String eventType, boolean success) {
        TransactionLog transactionLog = TransactionLog.builder()
                .orderId(deliveryEvent.getOrderId())
                .eventType(eventType)
                .payload(toJson(deliveryEvent))
                .result(success ? "SUCCESS" : "FAILURE")
                .build();

        transactionLogRepository.save(transactionLog);
    }

    private String toJson(DeliveryEvent deliveryEvent) {
        try {
            return objectMapper.writeValueAsString(deliveryEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event to JSON", e);
        }
    }
}
