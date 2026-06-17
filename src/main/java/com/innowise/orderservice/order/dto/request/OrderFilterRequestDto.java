package com.innowise.orderservice.order.dto.request;

import com.innowise.orderservice.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for filtering orders")
public class OrderFilterRequestDto {

    @Schema(
            description = "Return orders created from specified date and time",
            example = "2026-06-01T00:00:00"
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdFrom;

    @Schema(
            description = "Return orders created before specified date and time",
            example = "2026-06-30T23:59:59"
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdTo;

    @Schema(
            description = "Order statuses",
            example = "CREATED,PAID"
    )
    private Set<OrderStatus> statuses;
}