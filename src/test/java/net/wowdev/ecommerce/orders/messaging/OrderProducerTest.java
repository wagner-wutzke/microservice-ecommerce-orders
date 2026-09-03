package net.wowdev.ecommerce.orders.messaging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;
import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

class OrderProducerTest {

  @Test
  void publishesOrderCreatedEventWithEventIdAsKey() {
    final KafkaTemplate<String, Object> template = mock(KafkaTemplate.class);
    final OrderProducer producer = new OrderProducer(template, "orders.v1");
    final UUID eventId = UUID.randomUUID();
    final OrderCreatedEvent event =
        new OrderCreatedEvent(
            eventId,
            "TX-1",
            null,
            Instant.parse("2026-01-01T00:00:00Z"),
            OrderProducer.ORIGIN_SERVICE);

    producer.publish(event);

    verify(template).send("orders.v1", eventId.toString(), event);
  }
}
