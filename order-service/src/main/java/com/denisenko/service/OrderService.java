package com.denisenko.service;

import com.denisenko.dto.OrderDto;
import com.denisenko.dto.OrderLineItemDto;
import com.denisenko.model.Order;
import com.denisenko.model.OrderLineItem;
import com.denisenko.model.OrderStatus;
import com.denisenko.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.denisenko.model.OrderStatus.PENDING;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public OrderDto createOrder(OrderDto orderDto, String idempotencyKey) {
        Order order = mapToEntity(orderDto);
        order.setStatus(PENDING);
        orderRepository.save(order);
        redisTemplate.opsForValue().set(idempotencyKey, String.valueOf(order.getId()), Duration.ofMinutes(10));
        orderDto.setId(order.getId());
        orderDto.setStatus(order.getStatus().name());
        eventPublisher.publishEvent(orderDto);
        return orderDto;
    }

    public OrderDto getOrderByIdempotencyKey(String idempotencyKey) {
        String orderId = redisTemplate.opsForValue().get(idempotencyKey);
        return orderId != null ? getOrder(Long.parseLong(orderId)) : null;
    }

    public OrderDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        return mapToDto(order);
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) {
        orderRepository.updateOrderStatusById(orderId, status);
    }

    private Order mapToEntity(OrderDto orderDto) {
        return Order.builder()
                .orderLineItems(orderDto.getOrderLineItems().stream().map(this::mapToEntity).toList())
                .build();
    }

    private OrderDto mapToDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .status(order.getStatus().name())
                .orderLineItems(order.getOrderLineItems().stream().map(this::mapToDto).toList())
                .build();
    }

    private OrderLineItem mapToEntity(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .itemId(orderLineItemDto.getItemId())
                .quantity(orderLineItemDto.getQuantity())
                .build();
    }

    private OrderLineItemDto mapToDto(OrderLineItem orderLineItem) {
        return OrderLineItemDto.builder()
                .itemId(orderLineItem.getItemId())
                .quantity(orderLineItem.getQuantity())
                .build();
    }
}
