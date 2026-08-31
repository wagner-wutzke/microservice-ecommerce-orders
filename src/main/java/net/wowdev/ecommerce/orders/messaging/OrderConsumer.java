package net.wowdev.ecommerce.orders.messaging;

import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.OrderProcessingCompletedEvent;
import net.wowdev.ecommerce.domain.events.OrderProcessingFailedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${app.kafka.order-events-topic}",
    containerFactory = "kafkaListenerContainerFactory")
public class OrderConsumer {

  @KafkaHandler
  public void handleOrderProcessingFailed(OrderProcessingFailedEvent event) {
    log.debug(">>>> Processing OrderProcessingFailedEvent: {}", event);
  }

  @KafkaHandler
  public void handleOrderProcessingCompleted(OrderProcessingCompletedEvent event) {
    log.debug(">>>> Processing OrderProcessingCompletedEvent: {}", event);
  }

  @KafkaHandler(isDefault = true)
  public void handleUnknown(Object event) {
    log.debug(">>>> Received an unmapped event type {}: {}", event.getClass(), event);
  }
}
