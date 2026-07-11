package com.innowise.orderservice.order.controller;

import com.innowise.orderservice.client.user.dto.UserInfoResponseDto;
import com.innowise.orderservice.client.user.feign.UserServiceClient;
import com.innowise.orderservice.common.AbstractIntegrationTest;
import com.innowise.orderservice.common.annotation.WithMockCustomUser;
import com.innowise.orderservice.common.kafka.KafkaConsumerFactory;
import com.innowise.orderservice.common.kafka.KafkaProducerFactory;
import com.innowise.orderservice.common.kafka.KafkaTestConsumer;
import com.innowise.orderservice.item.entity.Item;
import com.innowise.orderservice.item.repository.ItemRepository;
import com.innowise.orderservice.kafka.event.CreateOrderEvent;
import com.innowise.orderservice.kafka.event.PaymentCompletedEvent;
import com.innowise.orderservice.kafka.event.PaymentStatus;
import com.innowise.orderservice.kafka.properties.KafkaTopics;
import com.innowise.orderservice.order.dto.request.CreateOrderRequestDto;
import com.innowise.orderservice.order.dto.request.UpdateOrderRequestDto;
import com.innowise.orderservice.order.entity.Order;
import com.innowise.orderservice.order.entity.OrderStatus;
import com.innowise.orderservice.order.repository.OrderRepository;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.innowise.orderservice.common.wiremock.UserServiceWireMockStub.*;
import static com.innowise.orderservice.order.testclasses.OrderTestDataFactory.createOrderRequest;
import static com.innowise.orderservice.order.testclasses.OrderTestDataFactory.updateOrderRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Value("${gateway.api-key}")
    private String gatewayApiKey;

    private KafkaTestConsumer<CreateOrderEvent> orderEventConsumer;

    private KafkaTestConsumer<PaymentCompletedEvent> paymentEventConsumer;

    @BeforeEach
    public void setUp() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        orderEventConsumer = new KafkaTestConsumer<>(
                KafkaConsumerFactory.createConsumer(
                        kafkaContainer,
                        CreateOrderEvent.class,
                        "order-test-" + UUID.randomUUID()
                )
        );
        orderEventConsumer.subscribe(KafkaTopics.ORDER_CREATED);

        paymentEventConsumer = new KafkaTestConsumer<>(
                KafkaConsumerFactory.createConsumer(
                        kafkaContainer,
                        PaymentCompletedEvent.class,
                        "payment-test-" + UUID.randomUUID()
                )
        );
        paymentEventConsumer.subscribe(KafkaTopics.PAYMENT_COMPLETED);
    }

    @AfterEach
    void tearDown() {
        orderEventConsumer.close();
        paymentEventConsumer.close();
    }

    @Test
    @DisplayName("Should create order and publish CreateOrderEvent to Kafka")
    @WithMockCustomUser
    void create_ShouldCreateOrderAndPublishEvent() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Item item = itemRepository.save(
                Item.builder()
                        .name("IPhone 17")
                        .price(BigDecimal.valueOf(3000))
                        .build()
        );

        stubUser(wireMockServer, userId);

        CreateOrderRequestDto request = createOrderRequest(userId, item.getId());

        mockMvc.perform(post("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.order.userId").value(userId.toString()))
                .andExpect(jsonPath("$.user.id").value(userId.toString()));

        CreateOrderEvent event = orderEventConsumer.receive();
        assertThat(event).isNotNull();
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(6000));

        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
        assertThat(orders.getFirst().getId()).isEqualTo(event.getOrderId());
    }

    @Test
    @DisplayName("Should process PaymentCompletedEvent with SUCCESS and update order status to PROCESSING")
    @WithMockCustomUser
    void updateStatus_WhenPaymentSuccess_ShouldUpdateToProcessing() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Order order = orderRepository.save(
                Order.builder()
                        .userId(userId)
                        .status(OrderStatus.CREATED)
                        .deleted(false)
                        .totalPrice(BigDecimal.valueOf(150.00))
                        .build()
        );

        PaymentCompletedEvent paymentEvent = PaymentCompletedEvent.builder()
                .orderId(order.getId())
                .status(PaymentStatus.SUCCESS)
                .build();

        KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate =
                KafkaProducerFactory.createKafkaTemplate(kafkaContainer);

        kafkaTemplate.send(
                KafkaTopics.PAYMENT_COMPLETED,
                order.getId().toString(),
                paymentEvent
        ).get(10, TimeUnit.SECONDS);

        await().atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(500))
                .until(() -> orderRepository.findById(order.getId())
                        .map(o -> o.getStatus() == OrderStatus.PROCESSING)
                        .orElse(false));

        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    @DisplayName("Should process PaymentCompletedEvent with FAILED and update order status to CANCELLED")
    @WithMockCustomUser
    void updateStatus_WhenPaymentFailed_ShouldUpdateToCancelled() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Order order = orderRepository.save(
                Order.builder()
                        .userId(userId)
                        .status(OrderStatus.CREATED)
                        .deleted(false)
                        .totalPrice(BigDecimal.valueOf(150.00))
                        .build()
        );

        PaymentCompletedEvent paymentEvent = PaymentCompletedEvent.builder()
                .orderId(order.getId())
                .status(PaymentStatus.FAILED)
                .build();

        KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate =
                KafkaProducerFactory.createKafkaTemplate(kafkaContainer);

        kafkaTemplate.send(
                KafkaTopics.PAYMENT_COMPLETED,
                order.getId().toString(),
                paymentEvent
        ).get(10, TimeUnit.SECONDS);

        await().atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(500))
                .until(() -> orderRepository.findById(order.getId())
                        .map(o -> o.getStatus() == OrderStatus.CANCELLED)
                        .orElse(false));

        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should not change status when duplicate payment event received")
    @WithMockCustomUser
    void updateStatus_WhenDuplicateEvent_ShouldNotChangeStatus() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Order order = orderRepository.save(
                Order.builder()
                        .userId(userId)
                        .status(OrderStatus.PROCESSING)
                        .deleted(false)
                        .totalPrice(BigDecimal.valueOf(150.00))
                        .build()
        );

        PaymentCompletedEvent paymentEvent = PaymentCompletedEvent.builder()
                .orderId(order.getId())
                .status(PaymentStatus.SUCCESS)
                .build();

        KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate =
                KafkaProducerFactory.createKafkaTemplate(kafkaContainer);

        kafkaTemplate.send(
                KafkaTopics.PAYMENT_COMPLETED,
                order.getId().toString(),
                paymentEvent
        ).get(10, TimeUnit.SECONDS);

        Thread.sleep(3000);

        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    @DisplayName("Should return 400 when item list is empty")
    @WithMockCustomUser
    void create_ShouldReturn400_WhenEmptyItems() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        stubUser(wireMockServer, userId);

        String request = """
                {
                    "userId": "%s",
                    "itemIds": []
                }
                """.formatted(userId.toString());

        mockMvc.perform(post("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when userId is null")
    @WithMockCustomUser
    void create_ShouldReturn400_WhenUserIdNull() throws Exception {
        String request = """
                {
                    "userId": null,
                    "itemIds": ["%s"]
                }
                """.formatted(UUID.randomUUID().toString());

        mockMvc.perform(post("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 when item not found")
    @WithMockCustomUser
    void create_ShouldReturn404_WhenItemNotFound() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID nonExistentItemId = UUID.randomUUID();

        stubUser(wireMockServer, userId);

        CreateOrderRequestDto request = createOrderRequest(userId, nonExistentItemId);

        mockMvc.perform(post("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should return 403 when creating order for another user")
    @WithMockCustomUser
    void create_ShouldReturn403_WhenCreatingForOtherUser() throws Exception {
        UUID otherUserId = UUID.randomUUID();

        Item item = itemRepository.save(
                Item.builder()
                        .name("IPhone 17")
                        .price(BigDecimal.valueOf(3000))
                        .build()
        );

        CreateOrderRequestDto request = createOrderRequest(otherUserId, item.getId());

        mockMvc.perform(post("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get order by id")
    @WithMockCustomUser
    void getById_ShouldReturnOrder() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Item item = itemRepository.save(
                Item.builder()
                        .name("IPhone 17")
                        .price(BigDecimal.valueOf(3000))
                        .build()
        );

        stubUser(wireMockServer, userId);

        CreateOrderRequestDto request = createOrderRequest(userId, item.getId());

        String response = mockMvc.perform(post("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID orderId = UUID.fromString(
                objectMapper.readTree(response)
                        .path("order")
                        .path("id")
                        .asText()
        );

        mockMvc.perform(get("/api/orders/{id}", orderId)
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.id").value(orderId.toString()));
    }

    @Test
    @DisplayName("Should return 404 when order not found by id")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void getById_ShouldReturn404_WhenOrderNotFound() throws Exception {
        UUID nonExistentOrderId = UUID.randomUUID();

        mockMvc.perform(get("/api/orders/{id}", nonExistentOrderId)
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 403 when accessing another user's order")
    @WithMockCustomUser
    void getById_ShouldReturn403_WhenAccessingOtherUserOrder() throws Exception {
        UUID otherUserId = UUID.randomUUID();

        Order order = orderRepository.save(
                Order.builder()
                        .userId(otherUserId)
                        .status(OrderStatus.CREATED)
                        .deleted(false)
                        .totalPrice(BigDecimal.ZERO)
                        .build()
        );

        mockMvc.perform(get("/api/orders/{id}", order.getId())
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 when accessing deleted order")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void getById_ShouldReturn404_WhenOrderDeleted() throws Exception {
        UUID userId = UUID.randomUUID();

        Order order = orderRepository.save(
                Order.builder()
                        .userId(userId)
                        .status(OrderStatus.CREATED)
                        .deleted(true)
                        .totalPrice(BigDecimal.ZERO)
                        .build()
        );

        mockMvc.perform(get("/api/orders/{id}", order.getId())
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return current user orders")
    @WithMockCustomUser
    void getMyOrders_ShouldReturnOrders() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Item item = itemRepository.save(
                Item.builder()
                        .name("IPhone 17")
                        .price(BigDecimal.valueOf(3000))
                        .build()
        );

        stubUser(wireMockServer, userId);

        CreateOrderRequestDto request = createOrderRequest(userId, item.getId());

        mockMvc.perform(post("/api/orders")
                .header("X-Gateway-Key", gatewayApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        mockMvc.perform(get("/api/orders/my")
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("Should return empty list when user has no orders")
    @WithMockCustomUser
    void getMyOrders_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/orders/my")
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Admin should update order")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void update_ShouldUpdateOrder() throws Exception {
        UUID userId = UUID.randomUUID();

        Item item = itemRepository.save(
                Item.builder()
                        .name("IPhone 17")
                        .price(BigDecimal.valueOf(3000))
                        .build()
        );

        stubUser(wireMockServer, userId);

        Order order = orderRepository.save(
                Order.builder()
                        .userId(userId)
                        .status(OrderStatus.CREATED)
                        .deleted(false)
                        .totalPrice(BigDecimal.ZERO)
                        .build()
        );

        UpdateOrderRequestDto request = updateOrderRequest(item.getId());

        mockMvc.perform(patch("/api/orders/{id}", order.getId())
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.status").value("COMPLETED"));

        Order updated = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should return 403 when non-admin tries to update")
    @WithMockCustomUser
    void update_ShouldReturn403_WhenNotAdmin() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        UpdateOrderRequestDto request = updateOrderRequest(itemId);

        mockMvc.perform(patch("/api/orders/{id}", orderId)
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent order")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void update_ShouldReturn404_WhenOrderNotFound() throws Exception {
        UUID nonExistentOrderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        UpdateOrderRequestDto request = updateOrderRequest(itemId);

        mockMvc.perform(patch("/api/orders/{id}", nonExistentOrderId)
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Admin should delete order")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void delete_ShouldDeleteOrder() throws Exception {
        UUID userId = UUID.randomUUID();

        Order order = orderRepository.save(
                Order.builder()
                        .userId(userId)
                        .status(OrderStatus.CREATED)
                        .deleted(false)
                        .totalPrice(BigDecimal.ZERO)
                        .build()
        );

        mockMvc.perform(delete("/api/orders/{id}", order.getId())
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent order")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void delete_ShouldReturn404_WhenOrderNotFound() throws Exception {
        UUID nonExistentOrderId = UUID.randomUUID();

        mockMvc.perform(delete("/api/orders/{id}", nonExistentOrderId)
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 403 when non-admin tries to delete")
    @WithMockCustomUser
    void delete_ShouldReturn403_WhenNotAdmin() throws Exception {
        UUID orderId = UUID.randomUUID();

        mockMvc.perform(delete("/api/orders/{id}", orderId)
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin should get all orders")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void getAll_ShouldReturnOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin should filter orders by status")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void getAll_ShouldFilterByStatus() throws Exception {
        UUID userId = UUID.randomUUID();

        stubUser(wireMockServer, userId);

        orderRepository.save(Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .deleted(false)
                .totalPrice(BigDecimal.TEN)
                .build());

        orderRepository.save(Order.builder()
                .userId(userId)
                .status(OrderStatus.COMPLETED)
                .deleted(false)
                .totalPrice(BigDecimal.TEN)
                .build());

        mockMvc.perform(get("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey)
                        .param("statuses", "CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].order.status").value("CREATED"));
    }

    @Test
    @DisplayName("Admin should filter orders by date range")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void getAll_ShouldFilterByDateRange() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey)
                        .param("createdFrom", "2025-01-01T00:00:00")
                        .param("createdTo", "2025-12-31T23:59:59"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 403 when user tries get admin endpoint")
    @WithMockCustomUser
    void getAll_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return fallback response when user service is unavailable")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void create_ShouldUseFallback_WhenUserServiceUnavailable() throws Exception {
        UUID userId = UUID.randomUUID();

        Item item = itemRepository.save(
                Item.builder()
                        .name("IPhone 17")
                        .price(BigDecimal.valueOf(3000))
                        .build()
        );

        stubUserServiceUnavailable(wireMockServer, userId);

        CreateOrderRequestDto request = createOrderRequest(userId, item.getId());

        mockMvc.perform(post("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("Should not create order when user service is unavailable")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void create_ShouldNotCreateOrder_WhenUserServiceUnavailable() throws Exception {
        UUID userId = UUID.randomUUID();

        Item item = itemRepository.save(
                Item.builder()
                        .name("IPhone 17")
                        .price(BigDecimal.valueOf(3000))
                        .build()
        );

        stubUserServiceUnavailable(wireMockServer, userId);

        CreateOrderRequestDto request = createOrderRequest(userId, item.getId());

        mockMvc.perform(post("/api/orders")
                        .header("X-Gateway-Key", gatewayApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable());

        List<Order> orders = orderRepository.findAll();
        assertThat(orders).isEmpty();
    }

    @Test
    @DisplayName("Circuit breaker should open after multiple failures")
    void circuitBreaker_ShouldOpen_AfterMultipleFailures() {
        UUID userId = UUID.randomUUID();

        stubUserServiceUnavailable(wireMockServer, userId);

        for (int i = 0; i < 4; i++) {
            try {
                userServiceClient.getUserById(userId);
            } catch (FeignException e) {
            }
        }

        try {
            userServiceClient.getUserById(userId);
        } catch (Exception e) {
            assertThat(e).isInstanceOfAny(
                    FeignException.class,
                    RuntimeException.class
            );
        }

        wireMockServer.verify(5, getRequestedFor(urlEqualTo("/api/users/" + userId)));
    }

    @Test
    @DisplayName("Circuit breaker should recover after timeout")
    void circuitBreaker_ShouldRecover_AfterTimeout() throws Exception {
        UUID userId = UUID.randomUUID();

        stubUserServiceUnavailable(wireMockServer, userId);

        for (int i = 0; i < 5; i++) {
            try {
                userServiceClient.getUserById(userId);
            } catch (FeignException e) {
            }
        }

        Thread.sleep(6000);

        wireMockServer.resetAll();
        stubUser(wireMockServer, userId);

        UserInfoResponseDto user = userServiceClient.getUserById(userId);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userId);
    }
}