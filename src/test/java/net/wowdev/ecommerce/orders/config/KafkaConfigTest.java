package net.wowdev.ecommerce.orders.config;

import static org.assertj.core.api.Assertions.assertThat;

import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.test.util.ReflectionTestUtils;

class KafkaConfigTest {
    @Test
    void createsConfiguredProducerConsumerAndListenerFactory() {
        final KafkaConfig config = new KafkaConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");
        ReflectionTestUtils.setField(config, "consumerGroupId", "orders");
        ReflectionTestUtils.setField(config, "acks", "all");
        ReflectionTestUtils.setField(config, "deliveryTimeout", "30000");
        ReflectionTestUtils.setField(config, "linger", "0");
        ReflectionTestUtils.setField(config, "requestTimeout", "10000");
        ReflectionTestUtils.setField(config, "idempotency", true);
        ReflectionTestUtils.setField(config, "retries", 3);
        ReflectionTestUtils.setField(config, "trustedPackages", "net.wowdev.ecommerce.domain.events");

        final ProducerFactory<String, Object> producerFactory = config.orderProducerFactory();
        final ConsumerFactory<String, Object> consumerFactory = config.orderConsumerFactory();
        final KafkaTemplate<String, Object> template = config.orderKafkaTemplate(producerFactory);
        final ConcurrentKafkaListenerContainerFactory<String, Object> listenerFactory =
                config.kafkaListenerContainerFactory(consumerFactory, template);

        assertThat(producerFactory).isNotNull();
        assertThat(consumerFactory).isNotNull();
        assertThat(template).isNotNull();
        assertThat(listenerFactory.getConsumerFactory()).isSameAs(consumerFactory);
    }
}
