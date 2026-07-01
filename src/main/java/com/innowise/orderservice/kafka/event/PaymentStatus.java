package com.innowise.orderservice.kafka.event;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    SUCCESS,
    FAILED
}