package com.innowise.orderservice.client.user.feign;

import com.innowise.orderservice.client.user.exception.UserServiceException;
import com.innowise.orderservice.common.exception.ErrorMessages;
import feign.Response;
import feign.codec.ErrorDecoder;

public class UserServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() >= 500) {
            return defaultErrorDecoder.decode(methodKey, response);
        }

        return switch (response.status()) {
            case 404 -> new UserServiceException(ErrorMessages.USER_NOT_FOUND);
            case 403 -> new UserServiceException(ErrorMessages.ACCESS_DENIED);
            case 401 -> new UserServiceException(ErrorMessages.UNAUTHORIZED);
            default -> new UserServiceException(ErrorMessages.USER_SERVICE_UNAVAILABLE);
        };
    }
}
