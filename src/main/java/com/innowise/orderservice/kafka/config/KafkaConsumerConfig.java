package com.innowise.orderservice.kafka.config;

import com.innowise.orderservice.kafka.event.PaymentCompletedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, PaymentCompletedEvent> consumerFactory(KafkaProperties kafkaProperties) {

        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties());

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JsonDeserializer<PaymentCompletedEvent> deserializer = new JsonDeserializer<>(PaymentCompletedEvent.class);

        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), deserializer);
    }

}