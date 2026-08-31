package net.wowdev.ecommerce.orders.messaging;

import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class OrderProducer {

  private final KafkaTemplate<String, Object> template;
  private final String ordersTopic;

  public OrderProducer(
      final KafkaTemplate<String, Object> template,
      @Value("${app.kafka.order-events-topic}") final String ordersTopic) {
    this.template = template;
    this.ordersTopic = ordersTopic;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void publish(final OrderProcessingStartedEvent event) {
    log.debug(">>>> Sending OrderProcessingStartedEvent: {}", event);
    template.send(ordersTopic, event.eventId().toString(), event);
  }
}
