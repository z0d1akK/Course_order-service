package com.innowise.orderservice.common.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.Objects;

public class KafkaTestConsumer<T> implements AutoCloseable {

    private final Consumer<String, T> consumer;

    public KafkaTestConsumer(Consumer<String, T> consumer) {
        this.consumer = consumer;
    }

    public void subscribe(String topic) {
        KafkaTestUtils.subscribe(consumer, topic);
    }

    public T receive() {
        return Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .until(() -> {
                    var records = consumer.poll(Duration.ofMillis(200));
                    if (records.isEmpty()) {
                        return null;
                    }
                    return records.iterator().next().value();
                }, Objects::nonNull);
    }

    @Override
    public void close() {
        consumer.close();
    }
}
