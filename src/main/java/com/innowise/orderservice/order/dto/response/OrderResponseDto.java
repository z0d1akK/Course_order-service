package com.innowise.orderservice.order.dto.response;

import com.innowise.orderservice.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    @Schema(example = "e73dcc73-e1db-4c3a-9246-0e1c2de79074")
    private UUID id;

    @Schema(example = "e73dcc73-e1db-4c3a-9246-0e1c2de79074")
    private UUID userId;

    @Schema(example = "CREATED")
    private OrderStatus status;

    @Schema(example = "100.10")
    private BigDecimal totalPrice;

    private List<OrderItemResponseDto> items;

    @Schema(example = "2026-06-14T15:00:00")
    private LocalDateTime createdAt;

    @Schema(example = "2026-06-14T15:30:00")
    private LocalDateTime updatedAt;
}