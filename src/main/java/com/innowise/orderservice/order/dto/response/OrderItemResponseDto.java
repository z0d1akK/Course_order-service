package com.innowise.orderservice.order.dto.response;

import com.innowise.orderservice.item.dto.response.ItemResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDto {

    private ItemResponseDto item;

    @Schema(example = "69")
    private Integer quantity;
}