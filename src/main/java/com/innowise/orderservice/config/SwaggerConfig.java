package com.innowise.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .version("1.0")
                        .description("REST API for Order Service")
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .schemaRequirement(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                )
                .addServersItem(createGatewayServer());
    }

    private Server createGatewayServer() {
        Server server = new Server();
        server.setDescription("API Gateway");
        String baseUrl = getGatewayBaseUrl();
        server.setUrl(baseUrl);
        return server;
    }

    private String getGatewayBaseUrl() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return "http://localhost:8084";
        }

        HttpServletRequest request = attributes.getRequest();

        String forwardedHost = request.getHeader("X-Forwarded-Host");
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String forwardedPort = request.getHeader("X-Forwarded-Port");

        if (forwardedHost != null) {
            String protocol = forwardedProto != null ? forwardedProto : "http";
            String port = "";

            if (forwardedPort != null &&
                    !"80".equals(forwardedPort) &&
                    !"443".equals(forwardedPort)) {
                port = ":" + forwardedPort;
            }

            return protocol + "://" + forwardedHost + port;
        }

        return "http://localhost:8084";
    }
}
