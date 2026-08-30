package net.wowdev.ecommerce.orders.messaging;

import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderProducerTest {

    @Test
    void publishesOrderCreationStartedEventWithEventIdAsKey() {
        final KafkaTemplate<String, Object> template = mock(KafkaTemplate.class);
        final OrderProducer producer = new OrderProducer(template, "orders.v1");
        final UUID eventId = UUID.randomUUID();
        final OrderProcessingStartedEvent event = new OrderProcessingStartedEvent(
                eventId, "TX-1", null, Instant.parse("2026-01-01T00:00:00Z"));

        producer.publish(event);

        verify(template).send("orders.v1", eventId.toString(), event);
    }

}
