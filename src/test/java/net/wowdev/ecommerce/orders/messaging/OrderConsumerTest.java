package net.wowdev.ecommerce.orders.messaging;

import net.wowdev.ecommerce.domain.events.OrderProcessingCompletedEvent;
import net.wowdev.ecommerce.domain.events.OrderProcessingFailedEvent;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class OrderConsumerTest {

    private final OrderConsumer consumer = new OrderConsumer();

    @Test
    void handlesFailedOrders() {
        consumer.handleOrderProcessingFailed(mock(OrderProcessingFailedEvent.class));
    }

    @Test
    void handlesCompletedOrders() {
        consumer.handleOrderProcessingCompleted(mock(OrderProcessingCompletedEvent.class));
    }

    @Test
    void handlesStartedOrders() {
        consumer.handleOrderProcessingStarted(mock(OrderProcessingStartedEvent.class));
    }

    @Test
    void handlesUnknownEvents() {
        consumer.handleUnknown(new Object());
    }
}
