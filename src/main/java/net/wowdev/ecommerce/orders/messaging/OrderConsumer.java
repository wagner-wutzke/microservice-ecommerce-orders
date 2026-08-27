package net.wowdev.ecommerce.orders.messaging;

import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.OrderCompletedEvent;
import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
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

//    @KafkaListener(topics = "${app.kafka.order-events-topic}",
//            groupId = "${spring.kafka.consumer.group-id}")
//    public void consume(final OrderCreatedEvent event) {
//        log.info(">> Consumed order change event {}", event.orderDTO().toString());
//    }

    @KafkaHandler
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println(">>>> Processing Created Order: " + event.orderDTO().toString());
    }

    @KafkaHandler
    public void handleOrderCancelled(OrderFailedEvent event) {
        System.out.println(">>>> Processing Failed Order: " + event.orderId());
    }

    @KafkaHandler
    public void handleOrderCompleted(OrderCompletedEvent event) {
        System.out.println(">>>> Processing Completed Order: " + event.orderDTO().toString());
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        System.out.println(">>>> Received an unmapped event type: " + event.getClass());
    }


}
