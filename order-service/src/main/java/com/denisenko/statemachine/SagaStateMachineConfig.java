package com.denisenko.statemachine;

import com.denisenko.model.OrderStatus;
import com.denisenko.statemachine.action.ActionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static com.denisenko.model.OrderStatus.*;
import static com.denisenko.statemachine.OrderEvent.*;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class SagaStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatus, OrderEvent> {
    private final ActionFactory actionFactory;

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states) throws Exception {
        states
                .withStates()
                .initial(PENDING)
                .end(COMPLETED)
                .end(FAILED)
                .states(EnumSet.allOf(OrderStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PENDING).target(WAITING_FOR_STOCK).event(RESERVE_STOCK).action(actionFactory.getAction(RESERVE_STOCK))
                .and()
                .withExternal()
                .source(WAITING_FOR_STOCK).target(WAITING_FOR_DELIVERY).event(BOOK_DELIVERY).action(actionFactory.getAction(BOOK_DELIVERY))
                .and()
                .withExternal()
                .source(WAITING_FOR_STOCK).target(FAILED).event(RESERVE_STOCK_FAILURE).action(actionFactory.getAction(RESERVE_STOCK_FAILURE))
                .and()
                .withExternal()
                .source(WAITING_FOR_DELIVERY).target(WAITING_FOR_PAYMENT).event(PAYMENT).action(actionFactory.getAction(PAYMENT))
                .and()
                .withExternal()
                .source(WAITING_FOR_DELIVERY).target(FAILED).event(BOOK_DELIVERY_FAILURE).action(actionFactory.getAction(BOOK_DELIVERY_FAILURE))
                .and()
                .withExternal()
                .source(WAITING_FOR_PAYMENT).target(COMPLETED).event(COMPLETE)
                .and()
                .withExternal()
                .source(WAITING_FOR_PAYMENT).target(FAILED).event(PAYMENT_FAILURE).action(actionFactory.getAction(PAYMENT_FAILURE));
    }
}
