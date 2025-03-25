package com.denisenko.controller;

import com.denisenko.dto.DeliverySlotDto;
import com.denisenko.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery/slots")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<List<DeliverySlotDto>> addSlots(@RequestBody List<DeliverySlotDto> deliverySlotsDto) {
        return ResponseEntity.ok(deliveryService.addSlots(deliverySlotsDto));
    }

    @GetMapping
    public ResponseEntity<List<DeliverySlotDto>> getSlots() {
        return ResponseEntity.ok(deliveryService.getSlots());
    }
}
