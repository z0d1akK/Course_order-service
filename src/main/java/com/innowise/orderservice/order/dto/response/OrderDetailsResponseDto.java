package com.innowise.orderservice.order.dto.response;

import com.innowise.orderservice.client.user.dto.UserInfoResponseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailsResponseDto {

    private OrderResponseDto order;

    private UserInfoResponseDto user;
}