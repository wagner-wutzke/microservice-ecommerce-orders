package net.wowdev.ecommerce.orders.messaging;

import static org.mockito.Mockito.mock;

import net.wowdev.ecommerce.domain.events.OrderProcessingCompletedEvent;
import net.wowdev.ecommerce.domain.events.OrderProcessingFailedEvent;
import org.junit.jupiter.api.Test;

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
  void handlesUnknownEvents() {
    consumer.handleUnknown(new Object());
  }
}
