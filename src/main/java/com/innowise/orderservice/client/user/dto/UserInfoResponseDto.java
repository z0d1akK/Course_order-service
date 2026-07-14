package com.innowise.orderservice.client.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponseDto {

    @Schema(example = "e73dcc73-e1db-4c3a-9246-0e1c2de79074")
    private UUID id;

    @Schema(example = "name")
    private String name;

    @Schema(example = "surname")
    private String surname;

    @Schema(example = "2005-05-05")
    private LocalDate birthDate;

    @Schema(example = "user@gmail.com")
    private String email;

    @Schema(example = "true")
    private Boolean active;

    @Schema(example = "2026-05-16T15:00:00")
    private LocalDateTime createdAt;

    @Schema(example = "2026-05-16T15:30:00")
    private LocalDateTime updatedAt;

    private List<PaymentCardResponseDto> paymentCards;
}