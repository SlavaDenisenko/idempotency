package com.denisenko.service;

import com.denisenko.dto.DeliverySlotDto;
import com.denisenko.model.DeliverySlot;
import com.denisenko.repository.DeliverySlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliverySlotRepository deliverySlotRepository;

    public List<DeliverySlotDto> addSlots(List<DeliverySlotDto> deliverySlotsDto) {
        List<DeliverySlot> deliverySlots = new ArrayList<>();
        deliverySlotsDto.forEach(deliverySlotDto -> {
            DeliverySlot deliverySlot = mapToEntity(deliverySlotDto);
            deliverySlot.setReserved(false);
            deliverySlots.add(deliverySlot);
        });
        return deliverySlotRepository.saveAll(deliverySlots).stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<DeliverySlotDto> getSlots() {
        return deliverySlotRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public boolean reserveSlot(Long orderId, LocalDateTime deliveryTime) {
        Optional<DeliverySlot> optionalSlot = deliverySlotRepository.findFirstByDeliveryTimeAndReservedFalse(deliveryTime);
        if (optionalSlot.isPresent()) {
            DeliverySlot slot = optionalSlot.get();
            slot.setReserved(true);
            slot.setOrderId(orderId);
            deliverySlotRepository.save(slot);
            return true;
        }
        return false;
    }

    public boolean cancelReservation(Long orderId) {
        Optional<DeliverySlot> optionalSlot = deliverySlotRepository.findFirstByOrderId(orderId);
        if (optionalSlot.isPresent()) {
            DeliverySlot slot = optionalSlot.get();
            slot.setReserved(false);
            slot.setOrderId(null);
            deliverySlotRepository.save(slot);
            return true;
        }
        return false;
    }

    private DeliverySlot mapToEntity(DeliverySlotDto deliverySlotDto) {
        return DeliverySlot.builder()
                .courierId(deliverySlotDto.getCourierId())
                .deliveryTime(deliverySlotDto.getDeliveryTime())
                .build();
    }

    private DeliverySlotDto mapToDto(DeliverySlot deliverySlot) {
        return DeliverySlotDto.builder()
                .courierId(deliverySlot.getCourierId())
                .deliveryTime(deliverySlot.getDeliveryTime())
                .reserved(deliverySlot.isReserved())
                .orderId(deliverySlot.getOrderId())
                .build();
    }
}
