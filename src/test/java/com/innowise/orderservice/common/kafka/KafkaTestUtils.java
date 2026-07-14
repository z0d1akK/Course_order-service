package com.innowise.orderservice.common.kafka;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.Consumer;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.Collections;

@UtilityClass
public class KafkaTestUtils {

    public static <T> void subscribe(Consumer<String, T> consumer, String topic) {
        consumer.subscribe(Collections.singletonList(topic));

        Awaitility.await().atMost(Duration.ofSeconds(5))
                .until(() -> {
                    consumer.poll(Duration.ofMillis(100));
                    return !consumer.assignment().isEmpty();
                });
    }
}
