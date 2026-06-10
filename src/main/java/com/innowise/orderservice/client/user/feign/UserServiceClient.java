package com.innowise.orderservice.client.user.feign;

import com.innowise.orderservice.client.user.config.FeignClientConfiguration;
import com.innowise.orderservice.client.user.dto.UserInfoResponseDto;
import com.innowise.orderservice.client.user.fallback.UserServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${user-service.url}",
        configuration = FeignClientConfiguration.class,
        fallbackFactory = UserServiceFallbackFactory.class
)
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    UserInfoResponseDto getUserById(@PathVariable UUID id);
}