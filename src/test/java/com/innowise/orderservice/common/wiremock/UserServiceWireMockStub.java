package com.innowise.orderservice.common.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.UUID;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public final class UserServiceWireMockStub {

    private UserServiceWireMockStub() { }

    public static void stubUser(WireMockServer wireMockServer, UUID userId) {
        wireMockServer.stubFor(
                get(urlEqualTo("/api/users/" + userId))
                        .willReturn(
                                okJson("""
                                        {
                                          "id":"%s",
                                          "name":"test",
                                          "surname":"test",
                                          "email":"test@example.com"
                                        }
                                        """.formatted(userId))
                        )
        );
    }

    public static void stubUserServiceUnavailable(WireMockServer wireMockServer, UUID userId) {
        wireMockServer.stubFor(
                get(urlEqualTo("/api/users/" + userId))
                        .willReturn(aResponse().withStatus(503))
        );
    }
}