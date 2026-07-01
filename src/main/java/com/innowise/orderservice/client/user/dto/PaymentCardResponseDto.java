package com.innowise.orderservice.client.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCardResponseDto {

    @Schema(example = "e73dcc73-e1db-4c3a-9246-0e1c2de79074")
    private UUID id;

    @Schema(example = "1111222233334444")
    private String number;

    @Schema(example = "11/11")
    private String expirationDate;

    @Schema(example = "true")
    private Boolean active;
}