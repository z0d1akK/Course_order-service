package com.innowise.orderservice.kafka.exception;

import com.innowise.orderservice.common.exception.BusinessException;

public class KafkaPublishException extends BusinessException {

    public KafkaPublishException(String message) {
        super(message);
    }
}