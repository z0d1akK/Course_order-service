package com.innowise.orderservice.kafka.event;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderEvent {

    private UUID orderId;

    private UUID userId;

    private BigDecimal totalPrice;
}