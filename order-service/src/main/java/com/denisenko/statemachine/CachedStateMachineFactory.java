package com.denisenko.statemachine;

import com.denisenko.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CachedStateMachineFactory {
    private final StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory;
    private final Map<Long, StateMachine<OrderStatus, OrderEvent>> cache = new ConcurrentHashMap<>();

    public StateMachine<OrderStatus, OrderEvent> getStateMachine(Long orderId) {
        return cache.computeIfAbsent(orderId, id -> stateMachineFactory.getStateMachine(id.toString()));
    }

    public void removeStateMachine(Long orderId) {
        cache.remove(orderId);
    }
}
