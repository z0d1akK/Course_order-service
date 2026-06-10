package com.innowise.orderservice.client.user.fallback;

import com.innowise.orderservice.client.user.exception.UserServiceUnavailableException;
import com.innowise.orderservice.client.user.feign.UserServiceClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallbackFactory implements FallbackFactory<UserServiceClient> {

    @Override
    public UserServiceClient create(Throwable cause) {

        return id -> {
            throw new UserServiceUnavailableException();
        };
    }
}