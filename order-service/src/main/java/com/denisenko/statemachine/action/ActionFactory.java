package com.denisenko.statemachine.action;

import com.denisenko.statemachine.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActionFactory {
    private final List<AbstractAction> actions;

    public AbstractAction getAction(OrderEvent orderEvent) {
        return actions.stream()
                .filter(action -> action.supports(orderEvent))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No action found for event: " + orderEvent));
    }
}
