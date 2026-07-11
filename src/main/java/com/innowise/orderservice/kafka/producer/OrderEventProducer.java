package com.innowise.orderservice.kafka.producer;

import com.innowise.orderservice.kafka.event.CreateOrderEvent;
import com.innowise.orderservice.kafka.exception.KafkaPublishException;
import com.innowise.orderservice.kafka.properties.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, CreateOrderEvent> kafkaTemplate;

    public void publishOrderCreatedEvent(CreateOrderEvent event) {
        try{
            CompletableFuture<SendResult<String, CreateOrderEvent>> future =
                    kafkaTemplate.send(
                            KafkaTopics.ORDER_CREATED,
                            event.getOrderId().toString(),
                            event
                    );

            future.whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error(
                            "Failed to publish order event. OrderId={}",
                            event.getOrderId(),
                            exception
                    );
                    return;
                }

                log.info(
                        "Order event published successfully. OrderId={}, partition={}, offset={}",
                        event.getOrderId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            });
        } catch (Exception e) {
            throw new KafkaPublishException(String.format(
                    "Failed to publish order event for order %s",
                    event.getOrderId())
            );
        }

    }
}