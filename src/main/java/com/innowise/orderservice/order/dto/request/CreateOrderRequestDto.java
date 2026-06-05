package com.innowise.orderservice.order.dto.request;

import com.innowise.orderservice.common.validation.ValidationMessages;
import com.innowise.orderservice.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for creating order")
public class CreateOrderRequestDto {

    @Schema(description = "User identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = ValidationMessages.ORDER_USER_ID_REQUIRED)
    private UUID userId;

    @Schema(description = "Order status", example = "CREATED")
    @NotNull(message = ValidationMessages.ORDER_STATUS_REQUIRED)
    private OrderStatus status;

    @Valid
    @NotEmpty(message = ValidationMessages.ORDER_ITEMS_REQUIRED)
    private List<OrderItemRequestDto> items;
}