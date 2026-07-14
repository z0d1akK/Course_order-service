package com.innowise.orderservice.item.dto.request;

import com.innowise.orderservice.common.validation.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for creating item")
public class CreateItemRequestDto {

    @Schema(description = "Item name", example = "Iphone 17")
    @NotBlank(message = ValidationMessages.ITEM_NAME_REQUIRED)
    @Size(min = 1, max = 255, message = ValidationMessages.ITEM_NAME_SIZE)
    private String name;

    @Schema(description = "Item price", example = "2999.99")
    @NotNull(message = ValidationMessages.ITEM_PRICE_REQUIRED)
    @DecimalMin(value = "0.01", message = ValidationMessages.ITEM_PRICE_POSITIVE)
    private BigDecimal price;
}