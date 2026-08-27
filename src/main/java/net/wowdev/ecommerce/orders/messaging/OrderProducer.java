package net.wowdev.ecommerce.orders.messaging;

import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class OrderProducer {
    private final KafkaTemplate<String, Object> template;
    private final String topic;

    public OrderProducer(final KafkaTemplate<String, Object> template,
                         @Value("${app.kafka.order-events-topic}") final String topic) {
        this.template = template;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishAfterCommit(final OrderCreatedEvent event) {
        System.out.println(">>>> Sending OrderCreatedEvent: " + event.orderDTO().toString());
        template.send(topic, event.orderDTO().getId().toString(), event);
    }
}
