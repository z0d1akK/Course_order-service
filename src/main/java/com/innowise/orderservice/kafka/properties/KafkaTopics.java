package com.innowise.orderservice.kafka.properties;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaTopics {

    public static final String ORDER_CREATED = "order-created";

    public static final String PAYMENT_COMPLETED = "payment-completed";
}