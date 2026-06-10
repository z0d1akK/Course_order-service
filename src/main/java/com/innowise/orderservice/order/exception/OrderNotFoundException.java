package com.innowise.orderservice.order.exception;

import com.innowise.orderservice.common.exception.ErrorMessages;
import com.innowise.orderservice.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class OrderNotFoundException extends ResourceNotFoundException {

    public OrderNotFoundException(UUID id) {
        super(ErrorMessages.ORDER_NOT_FOUND.formatted(id));
    }
}