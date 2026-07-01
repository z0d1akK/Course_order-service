package com.innowise.orderservice.item.exception;

import com.innowise.orderservice.common.exception.BusinessException;
import com.innowise.orderservice.common.exception.ErrorMessages;

public class ItemAlreadyExistsException extends BusinessException {

    public ItemAlreadyExistsException() {
        super(ErrorMessages.ITEM_ALREADY_EXISTS);
    }
}