package net.wowdev.ecommerce.orders.messaging;

import static org.mockito.Mockito.mock;

import net.wowdev.ecommerce.domain.events.OrderCompletedEvent;
import net.wowdev.ecommerce.domain.events.OrderFailedEvent;
import org.junit.jupiter.api.Test;

class OrderConsumerTest {

    private final OrderConsumer consumer = new OrderConsumer();

    @Test
    void handlesFailedOrders() {
        consumer.handleOrderCancelled(mock(OrderFailedEvent.class));
    }

    @Test
    void handlesCompletedOrders() {
        consumer.handleOrderCompleted(mock(OrderCompletedEvent.class));
    }

    @Test
    void handlesUnknownEvents() {
        consumer.handleUnknown(new Object());
    }
}
