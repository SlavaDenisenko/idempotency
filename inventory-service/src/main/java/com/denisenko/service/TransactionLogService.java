package com.denisenko.service;

import com.denisenko.event.InventoryEvent;
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

    public void logEvent(InventoryEvent inventoryEvent, String eventType, boolean success) {
        TransactionLog transactionLog = TransactionLog.builder()
                .orderId(inventoryEvent.getOrderId())
                .eventType(eventType)
                .payload(toJson(inventoryEvent))
                .result(success ? "SUCCESS" : "FAILURE")
                .build();

        transactionLogRepository.save(transactionLog);
    }

    private String toJson(InventoryEvent inventoryEvent) {
        try {
            return new ObjectMapper().writeValueAsString(inventoryEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event to JSON", e);
        }
    }
}
