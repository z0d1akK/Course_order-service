package com.innowise.orderservice.common.annotation;

import com.innowise.orderservice.common.WithMockCustomUserSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String userId() default "00000000-0000-0000-0000-000000000001";
    String email() default "test@example.com";
    String role() default "ROLE_USER";
}