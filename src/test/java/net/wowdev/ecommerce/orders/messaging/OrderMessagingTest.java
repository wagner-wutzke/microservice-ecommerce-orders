package net.wowdev.ecommerce.orders.messaging;

import static org.mockito.Mockito.verify;

import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
import net.wowdev.ecommerce.orders.TestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

class OrderMessagingTest {
    @Test
    void producerPublishesToConfiguredTopicUsingOrderIdAsKey() {
        final KafkaTemplate<String, Object> template = org.mockito.Mockito.mock(KafkaTemplate.class);
        final OrderProducer producer = new OrderProducer(template, "orders.v1");
        final OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                TestFixtures.orderDto(),
                TestFixtures.orderDto().getCreatedAt()
                );

        producer.publishAfterCommit(event);

        verify(template).send("orders.v1", event.orderDTO().getId().toString(), event);
    }

    @Test
    void consumerAcceptsAnEventWithoutStartingKafka() {
        final OrderConsumer consumer = new OrderConsumer();

        consumer.handleOrderCreated(new OrderCreatedEvent(
                UUID.randomUUID(),
                TestFixtures.orderDto(),
                TestFixtures.orderDto().getCreatedAt()));
    }
}
