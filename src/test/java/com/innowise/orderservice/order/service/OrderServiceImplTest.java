package com.innowise.orderservice.order.service;

import com.innowise.orderservice.client.user.dto.UserInfoResponseDto;
import com.innowise.orderservice.client.user.feign.UserServiceClient;
import com.innowise.orderservice.item.entity.Item;
import com.innowise.orderservice.item.exception.ItemNotFoundException;
import com.innowise.orderservice.item.repository.ItemRepository;
import com.innowise.orderservice.order.dto.request.CreateOrderRequestDto;
import com.innowise.orderservice.order.dto.request.OrderFilterRequestDto;
import com.innowise.orderservice.order.dto.request.OrderItemRequestDto;
import com.innowise.orderservice.order.dto.request.UpdateOrderRequestDto;
import com.innowise.orderservice.order.dto.response.OrderDetailsResponseDto;
import com.innowise.orderservice.order.dto.response.OrderResponseDto;
import com.innowise.orderservice.order.entity.Order;
import com.innowise.orderservice.order.entity.OrderStatus;
import com.innowise.orderservice.order.exception.OrderNotFoundException;
import com.innowise.orderservice.order.mapper.OrderDetailsMapper;
import com.innowise.orderservice.order.mapper.OrderMapper;
import com.innowise.orderservice.order.repository.OrderRepository;
import com.innowise.orderservice.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.innowise.orderservice.order.testclasses.OrderTestDataFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderDetailsMapper orderDetailsMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Should create order successfully")
    void create_ShouldCreateOrder() {
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        CreateOrderRequestDto request = createOrderRequest(userId, itemId);

        Item item = createItem(itemId);

        Order order = createOrder(orderId, userId);

        UserInfoResponseDto user = createUserResponse(userId);

        OrderResponseDto orderResponse = createOrderResponse(orderId, userId);

        OrderDetailsResponseDto response = createOrderDetailsResponse(orderId, userId);

        when(orderMapper.toEntity(request)).thenReturn(order);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(orderDetailsMapper.toResponse(orderResponse, user)).thenReturn(response);

        OrderDetailsResponseDto result = orderService.create(request);

        assertThat(result).isEqualTo(response);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw ItemNotFoundException when creating order with non-existing item")
    void create_WhenItemNotExists_ShouldThrowException() {
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        CreateOrderRequestDto request = createOrderRequest(userId, itemId);

        Order order = createOrder(UUID.randomUUID(), userId);

        when(orderMapper.toEntity(request)).thenReturn(order);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("Should return order when order exists")
    void getById_WhenOrderExists_ShouldReturnOrder() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Order order = createOrder(orderId, userId);

        UserInfoResponseDto user = createUserResponse(userId);

        OrderResponseDto orderResponse = createOrderResponse(orderId, userId);

        OrderDetailsResponseDto response = createOrderDetailsResponse(orderId, userId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(orderDetailsMapper.toResponse(orderResponse, user)).thenReturn(response);

        OrderDetailsResponseDto result = orderService.getById(orderId);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order does not exist")
    void getById_WhenOrderNotExists_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order is deleted")
    void getById_WhenOrderDeleted_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();

        Order order = createDeletedOrder(orderId, UUID.randomUUID());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getById(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("Should return all orders according to filter")
    void getAll_ShouldReturnOrders() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Order order = createOrder(orderId, userId);

        UserInfoResponseDto user = createUserResponse(userId);

        OrderResponseDto orderResponse = createOrderResponse(orderId, userId);

        OrderDetailsResponseDto response = createOrderDetailsResponse(orderId, userId);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> page = new PageImpl<>(java.util.List.of(order));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(orderDetailsMapper.toResponse(orderResponse, user)).thenReturn(response);

        Page<OrderDetailsResponseDto> result = orderService.getAll(new OrderFilterRequestDto(), pageable);

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return all orders for specified user")
    void getByUserId_ShouldReturnOrders() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Order order = createOrder(orderId, userId);

        UserInfoResponseDto user = createUserResponse(userId);

        OrderResponseDto orderResponse = createOrderResponse(orderId, userId);

        OrderDetailsResponseDto response = createOrderDetailsResponse(orderId, userId);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAllByUserIdAndDeletedFalse(userId, pageable)).thenReturn(page);
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(orderDetailsMapper.toResponse(orderResponse, user)).thenReturn(response);

        Page<OrderDetailsResponseDto> result = orderService.getByUserId(userId, pageable);

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should update order successfully")
    void update_ShouldUpdateOrder() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        UpdateOrderRequestDto request = UpdateOrderRequestDto.builder()
                .status(OrderStatus.PROCESSING)
                .items(List.of(
                        OrderItemRequestDto.builder()
                                .itemId(itemId)
                                .quantity(3)
                                .build()
                ))
                .build();

        Order order = createOrder(orderId, userId);

        Item item = createItem(itemId);

        UserInfoResponseDto user = createUserResponse(userId);

        OrderResponseDto orderResponse = createOrderResponse(orderId, userId);

        OrderDetailsResponseDto response = createOrderDetailsResponse(orderId, userId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderRepository.save(order)).thenReturn(order);
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(orderDetailsMapper.toResponse(orderResponse, user)).thenReturn(response);

        OrderDetailsResponseDto result = orderService.update(orderId, request);

        assertThat(result).isEqualTo(response);

        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getOrderItems().getFirst().getItem()).isEqualTo(item);
        assertThat(order.getOrderItems().getFirst().getQuantity()).isEqualTo(3);

        verify(orderRepository).findById(orderId);
        verify(orderMapper).updateEntity(request, order);
        verify(itemRepository).findById(itemId);
        verify(orderRepository).save(order);
        verify(userServiceClient).getUserById(userId);
        verify(orderMapper).toResponse(order);
        verify(orderDetailsMapper).toResponse(orderResponse, user);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when updating non-existing order")
    void update_WhenOrderNotExists_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();

        UpdateOrderRequestDto request = updateOrderRequest(UUID.randomUUID());

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.update(orderId, request))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ItemNotFoundException when updating order with non-existing item")
    void update_WhenItemNotExists_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        Order order = createOrder(orderId, userId);

        UpdateOrderRequestDto request = updateOrderRequest(itemId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.update(orderId, request))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("Should soft delete order successfully")
    void delete_ShouldSoftDeleteOrder() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Order order = createOrder(orderId, userId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.delete(orderId);

        verify(orderRepository).softDeleteById(eq(orderId), any());
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when deleting non-existing order")
    void delete_WhenOrderNotExists_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.delete(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }
}