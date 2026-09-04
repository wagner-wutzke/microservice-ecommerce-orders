package net.wowdev.ecommerce.orders.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.InventoryUpdateFailedEvent;
import net.wowdev.ecommerce.domain.events.OrderProcessingCompletedEvent;
import net.wowdev.ecommerce.domain.events.OrderProcessingFailedEvent;
import net.wowdev.ecommerce.orders.service.OrderService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = {"${app.kafka.orders-topic}", "${app.kafka.inventory-topic}"},
    containerFactory = "kafkaListenerContainerFactory")
public class OrderConsumer {

  private final OrderService orderService;

  @KafkaHandler
  public void handleOrderProcessingFailed(OrderProcessingFailedEvent event) {
    log.debug(
        ">>>> Processing OrderProcessingFailedEvent sent by {}. Event id: {}",
        event.origin(),
        event.eventId());
  }

  @KafkaHandler
  public void handleOrderProcessingCompleted(OrderProcessingCompletedEvent event) {
    log.debug(
        ">> Processing OrderProcessingCompletedEvent sent by {}. Event id: {}",
        event.origin(),
        event.eventId());
  }

  @KafkaHandler
  public void handleInventoryUpdateFailed(InventoryUpdateFailedEvent event) {
    log.debug(
        ">> Processing InventoryUpdateFailedEvent sent by {}. Event id {}",
        event.origin(),
        event.eventId());
    orderService.cancel(event.orderDTO(), event.reason());
  }

  @KafkaHandler(isDefault = true)
  public void handleUnknown(Object event) {
    log.debug(">> Received an unmapped event of type {}", event.getClass().getSimpleName());
  }
}
