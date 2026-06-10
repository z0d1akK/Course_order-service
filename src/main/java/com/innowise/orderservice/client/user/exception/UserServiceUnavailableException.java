package com.innowise.orderservice.client.user.exception;

import com.innowise.orderservice.common.exception.BusinessException;
import com.innowise.orderservice.common.exception.ErrorMessages;

public class UserServiceUnavailableException extends BusinessException {

    public UserServiceUnavailableException() {
        super(ErrorMessages.USER_SERVICE_UNAVAILABLE);
    }
}