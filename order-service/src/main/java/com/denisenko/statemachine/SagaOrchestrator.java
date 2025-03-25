package com.denisenko.statemachine;

import com.denisenko.dto.OrderDto;
import com.denisenko.event.ResultEvent;
import com.denisenko.model.OrderStatus;
import com.denisenko.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.denisenko.model.OrderStatus.*;
import static com.denisenko.statemachine.OrderEvent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestrator {
    private final CachedStateMachineFactory cachedStateMachineFactory;
    private final OrderService orderService;

    public void startOrderSaga(OrderDto orderDto) {
        StateMachine<OrderStatus, OrderEvent> stateMachine = cachedStateMachineFactory.getStateMachine(orderDto.getId());
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(accessor -> accessor.addStateMachineInterceptor(new StateMachineInterceptorAdapter<>() {
                    @Override
                    public void preStateChange(State<OrderStatus, OrderEvent> state, Message<OrderEvent> message,
                                               Transition<OrderStatus, OrderEvent> transition, StateMachine<OrderStatus, OrderEvent> stateMachine,
                                               StateMachine<OrderStatus, OrderEvent> rootStateMachine) {
                        orderService.updateOrderStatus(orderDto.getId(), state.getId());
                    }
                }));
        stateMachine.getExtendedState().getVariables().put("order", orderDto);
        stateMachine.startReactively().subscribe();
        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(RESERVE_STOCK).build())).subscribe();
    }

    public void handleResult(ResultEvent resultEvent) {
        StateMachine<OrderStatus, OrderEvent> stateMachine = cachedStateMachineFactory.getStateMachine(resultEvent.getOrderId());
        OrderEvent orderEvent = determineEvent(stateMachine.getState().getId(), resultEvent.getSuccess());
        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(orderEvent).build())).subscribe();
        if (isSagaCompleted(stateMachine.getState().getId())) {
            log.info("Saga for order with ID={} is over with status={}", resultEvent.getOrderId(), stateMachine.getState().getId());
            cachedStateMachineFactory.removeStateMachine(resultEvent.getOrderId());
        }
    }

    private OrderEvent determineEvent(OrderStatus status, boolean success) {
        switch (status) {
            case WAITING_FOR_STOCK -> {
                return success ? BOOK_DELIVERY : RESERVE_STOCK_FAILURE;
            }
            case WAITING_FOR_DELIVERY -> {
                return success ? PAYMENT : BOOK_DELIVERY_FAILURE;
            }
            case WAITING_FOR_PAYMENT -> {
                return success ? COMPLETE : PAYMENT_FAILURE;
            }
            default -> throw new IllegalStateException("Unexpected state: " + status);
        }
    }

    private boolean isSagaCompleted(OrderStatus status) {
        return status == COMPLETED || status == FAILED;
    }
}
