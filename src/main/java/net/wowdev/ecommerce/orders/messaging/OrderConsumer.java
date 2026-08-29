package net.wowdev.ecommerce.orders.messaging;

import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.OrderCompletedEvent;
import net.wowdev.ecommerce.domain.events.OrderFailedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
        groupId = "${spring.kafka.consumer.group-id}",
        topics = "${app.kafka.order-events-topic}",
        containerFactory = "kafkaListenerContainerFactory"
)
public class OrderConsumer {

    @KafkaHandler
    public void handleOrderCancelled(OrderFailedEvent event) {
        log.info(">>>> Processing FailedOrderEvent: {}", event);
    }

    @KafkaHandler
    public void handleOrderCompleted(OrderCompletedEvent event) {
        log.info(">>>> Processing CompletedOrderEvent: {}", event);
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.info(">>>> Received an unmapped event type: {}", event.getClass());
    }


}
