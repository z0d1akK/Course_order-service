package com.innowise.orderservice.client.user.config;

import com.innowise.orderservice.common.constants.Headers;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserContextFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();

        copyHeader(template, request, Headers.USER_ID);
        copyHeader(template, request, Headers.USER_ROLE);
        copyHeader(template, request, Headers.USER_LOGIN);
        copyHeader(template, request, Headers.GATEWAY_KEY);
    }

    private void copyHeader(RequestTemplate template, HttpServletRequest request, String header) {
        String value = request.getHeader(header);

        if (value != null) {
            template.header(header, value);
        }
    }
}