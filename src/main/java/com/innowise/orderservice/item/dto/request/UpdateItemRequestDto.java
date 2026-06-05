package com.innowise.orderservice.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for updating item")
public class UpdateItemRequestDto {

    @Schema(description = "Item name", example = "Iphone 18")
    private String name;

    @Schema(description = "Item price", example = "3499.99")
    private BigDecimal price;
}