package com.innowise.orderservice.order.mapper;


import com.innowise.orderservice.client.user.dto.UserInfoResponseDto;
import com.innowise.orderservice.config.MapStructConfig;
import com.innowise.orderservice.order.dto.response.OrderDetailsResponseDto;
import com.innowise.orderservice.order.dto.response.OrderResponseDto;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface OrderDetailsMapper {

    default OrderDetailsResponseDto toResponse(OrderResponseDto order, UserInfoResponseDto user) {
        return OrderDetailsResponseDto.builder().order(order).user(user).build();
    }
}