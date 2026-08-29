package net.wowdev.ecommerce.orders.messaging;

import net.wowdev.ecommerce.domain.dto.PaymentDTO;
import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
import net.wowdev.ecommerce.domain.events.PaymentCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderProducerTest {

    @Test
    void publishesOrderCreatedEventWithEventIdAsKey() {
        final KafkaTemplate<String, Object> template = mock(KafkaTemplate.class);
        final OrderProducer producer = new OrderProducer(template, "orders.v1");
        final UUID eventId = UUID.randomUUID();
        final OrderCreatedEvent event = new OrderCreatedEvent(
                eventId, "TX-1", null, Instant.parse("2026-01-01T00:00:00Z"));

        producer.publishOrderCreatedEvent(event);

        verify(template).send("orders.v1", eventId.toString(), event);
    }

    @Test
    void publishesPaymentCreatedEventWithEventIdAsKey() {
        final KafkaTemplate<String, Object> template = mock(KafkaTemplate.class);
        final OrderProducer producer = new OrderProducer(template, "orders.v1");
        final UUID eventId = UUID.randomUUID();
        final PaymentCreatedEvent event = new PaymentCreatedEvent(
                eventId, "TX-2", mock(PaymentDTO.class), Instant.parse("2026-01-01T00:00:00Z"));

        producer.publishPaymentCreatedEvent(event);

        verify(template).send("orders.v1", eventId.toString(), event);
    }
}
