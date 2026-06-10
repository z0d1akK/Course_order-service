package com.innowise.orderservice.item.exception;

import com.innowise.orderservice.common.exception.ErrorMessages;
import com.innowise.orderservice.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ItemNotFoundException extends ResourceNotFoundException {

    public ItemNotFoundException(UUID id) {
        super(ErrorMessages.ITEM_NOT_FOUND.formatted(id));
    }
}