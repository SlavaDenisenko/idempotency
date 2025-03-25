package com.denisenko.controller;

import com.denisenko.dto.OrderDto;
import com.denisenko.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                @RequestBody OrderDto orderDto) {
        OrderDto order = orderService.getOrderByIdempotencyKey(idempotencyKey);
        if (order == null)
            order = orderService.createOrder(orderDto, idempotencyKey);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}
