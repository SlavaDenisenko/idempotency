package com.denisenko.statemachine.action;

import com.denisenko.dto.OrderDto;
import com.denisenko.model.OrderStatus;
import com.denisenko.statemachine.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

@RequiredArgsConstructor
public abstract class AbstractAction implements Action<OrderStatus, OrderEvent> {
    protected final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderEvent orderEvent;

    protected abstract void executeAction(OrderDto orderDto);

    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> context) {
        OrderDto orderDto = context.getStateMachine().getExtendedState().get("order", OrderDto.class);
        executeAction(orderDto);
    }

    public boolean supports(OrderEvent orderEvent) {
        return this.orderEvent == orderEvent;
    }
}
