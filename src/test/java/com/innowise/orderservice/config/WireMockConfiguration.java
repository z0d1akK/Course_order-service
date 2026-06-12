package com.innowise.orderservice.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@TestConfiguration
public class WireMockConfiguration {

    private static volatile WireMockServer instance;

    public static WireMockServer getInstance() {
        if (instance == null) {
            synchronized (WireMockConfiguration.class) {
                if (instance == null) {
                    instance = new WireMockServer(options().dynamicPort());
                    instance.start();
                }
            }
        }
        return instance;
    }

    public static int getPort() {
        return getInstance().port();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        return getInstance();
    }
}