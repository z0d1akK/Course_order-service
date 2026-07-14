package com.innowise.orderservice.order.dto.request;

import com.innowise.orderservice.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for updating order")
public class UpdateOrderRequestDto {

    @Schema(description = "Order status", example = "PROCESSING")
    private OrderStatus status;

    @Valid
    private List<OrderItemRequestDto> items;
}