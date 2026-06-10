package com.innowise.orderservice.client.user.exception;

import com.innowise.orderservice.common.exception.BusinessException;

public class UserServiceException extends BusinessException {

    public UserServiceException(String message) {
        super(message);
    }
}