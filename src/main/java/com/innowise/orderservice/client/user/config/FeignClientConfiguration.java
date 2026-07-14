package com.innowise.orderservice.client.user.config;

import com.innowise.orderservice.client.user.feign.UserServiceErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignClientConfiguration {

    private final UserContextFeignInterceptor userContextFeignInterceptor;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new UserServiceErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return userContextFeignInterceptor;
    }
}