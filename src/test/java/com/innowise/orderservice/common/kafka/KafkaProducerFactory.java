package com.innowise.orderservice.common.kafka;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.kafka.KafkaContainer;

import java.util.Map;

@UtilityClass
public class KafkaProducerFactory {

    public static <T> KafkaTemplate<String, T> createKafkaTemplate(KafkaContainer kafkaContainer) {
        Map<String, Object> properties = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                ProducerConfig.ACKS_CONFIG, "all"
        );

        DefaultKafkaProducerFactory<String, T> factory = new DefaultKafkaProducerFactory<>(
                properties,
                new StringSerializer(),
                new JsonSerializer<>()
        );

        return new KafkaTemplate<>(factory);
    }
}
