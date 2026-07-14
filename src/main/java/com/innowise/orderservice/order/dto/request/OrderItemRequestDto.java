package com.innowise.orderservice.order.dto.request;

import com.innowise.orderservice.common.validation.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for order item")
public class OrderItemRequestDto {

    @Schema(description = "Item identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = ValidationMessages.ORDER_ITEM_ID_REQUIRED)
    private UUID itemId;

    @Schema(description = "Item quantity", example = "2")
    @NotNull(message = ValidationMessages.ORDER_ITEM_QUANTITY_REQUIRED)
    @Min(value = 1, message = ValidationMessages.ORDER_ITEM_QUANTITY_POSITIVE)
    private Integer quantity;
}