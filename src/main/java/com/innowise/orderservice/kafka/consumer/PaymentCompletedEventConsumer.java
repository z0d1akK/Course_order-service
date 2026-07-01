package com.innowise.orderservice.kafka.consumer;

import com.innowise.orderservice.kafka.event.PaymentCompletedEvent;
import com.innowise.orderservice.kafka.properties.KafkaTopics;
import com.innowise.orderservice.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletedEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = KafkaTopics.PAYMENT_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PaymentCompletedEvent event) {

        log.info(
                "Payment event received. OrderId={}, status={}",
                event.getOrderId(),
                event.getStatus()
        );

        orderService.updateStatus(event.getOrderId(), event.getStatus());

        log.info(
                "Order {} updated successfully.",
                event.getOrderId()
        );
    }
}