package com.denisenko;

import com.denisenko.model.DeliverySlot;
import com.denisenko.repository.DeliverySlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Import(ContainerConfig.class)
@TestPropertySource(properties = {
        "server.port=0",
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.kafka.bootstrap-servers=my-cluster:9092",
        "spring.kafka.topics.book-delivery=book-delivery",
        "spring.kafka.topics.delivery-compensate=delivery-compensate",
        "spring.kafka.topics.order-results=order-results",
        "spring.kafka.consumer.group-id=delivery-service-group"
})
public class DeliverySlotRepositoryTest {

    @Autowired
    private DeliverySlotRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        DeliverySlot deliverySlot1 = DeliverySlot.builder()
                .courierId(478999L)
                .deliveryTime(LocalDateTime.of(2024, 12, 31, 10, 0, 0))
                .reserved(false)
                .build();

        DeliverySlot deliverySlot2 = DeliverySlot.builder()
                .courierId(478999L)
                .deliveryTime(LocalDateTime.of(2024, 12, 31, 12, 0, 0))
                .reserved(true)
                .build();

        DeliverySlot deliverySlot3 = DeliverySlot.builder()
                .courierId(478999L)
                .deliveryTime(LocalDateTime.of(2024, 12, 31, 14, 0, 0))
                .reserved(false)
                .build();

        DeliverySlot deliverySlot4 = DeliverySlot.builder()
                .courierId(577332L)
                .deliveryTime(LocalDateTime.of(2024, 12, 31, 10, 0, 0))
                .reserved(true)
                .build();

        DeliverySlot deliverySlot5 = DeliverySlot.builder()
                .courierId(577332L)
                .deliveryTime(LocalDateTime.of(2024, 12, 31, 12, 0, 0))
                .reserved(false)
                .build();

        DeliverySlot deliverySlot6 = DeliverySlot.builder()
                .courierId(577332L)
                .deliveryTime(LocalDateTime.of(2024, 12, 31, 14, 0, 0))
                .reserved(false)
                .build();

        List<DeliverySlot> deliverySlots = List.of(deliverySlot1, deliverySlot2, deliverySlot3, deliverySlot4, deliverySlot5, deliverySlot6);
        repository.saveAll(deliverySlots);
    }

    @Test
    void testFindFirstByDeliveryTimeAndReservedFalse() {
        LocalDateTime deliveryTime = LocalDateTime.of(2024, 12, 31, 10, 0, 0);
        var slot = repository.findFirstByDeliveryTimeAndReservedFalse(deliveryTime);
        System.out.println(slot);
    }
}
