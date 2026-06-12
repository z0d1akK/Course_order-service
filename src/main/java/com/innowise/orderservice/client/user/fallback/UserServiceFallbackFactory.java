package com.innowise.orderservice.client.user.fallback;

import com.innowise.orderservice.client.user.exception.UserServiceException;
import com.innowise.orderservice.client.user.feign.UserServiceClient;
import com.innowise.orderservice.common.exception.ErrorMessages;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallbackFactory implements FallbackFactory<UserServiceClient> {

    @Override
    public UserServiceClient create(Throwable cause) {
        return id -> {
            if (cause instanceof FeignException.ServiceUnavailable) {
                throw new UserServiceException(ErrorMessages.USER_SERVICE_UNAVAILABLE);
            } else if (cause instanceof FeignException.InternalServerError) {
                throw new UserServiceException(ErrorMessages.USER_SERVICE_UNAVAILABLE);
            } else if (cause instanceof FeignException.NotFound) {
                throw new UserServiceException(ErrorMessages.USER_NOT_FOUND);
            } else if (cause instanceof UserServiceException) {
                throw (UserServiceException) cause;
            } else if (isCircuitBreakerOpen(cause)) {
                throw new UserServiceException(ErrorMessages.USER_SERVICE_UNAVAILABLE);
            } else {
                throw new UserServiceException(ErrorMessages.USER_SERVICE_UNAVAILABLE);
            }
        };
    }

    private boolean isCircuitBreakerOpen(Throwable cause) {
        if (cause == null) return false;

        String message = cause.getMessage() != null ? cause.getMessage() : "";
        String className = cause.getClass().getName();

        return message.contains("CircuitBreaker") && message.contains("open") ||
                className.contains("CallNotPermittedException") ||
                className.contains("CircuitBreakerOpenException");
    }
}