package com.innowise.orderservice.order.mapper;

import com.innowise.orderservice.config.MapStructConfig;
import com.innowise.orderservice.order.dto.request.CreateOrderRequestDto;
import com.innowise.orderservice.order.dto.request.UpdateOrderRequestDto;
import com.innowise.orderservice.order.dto.response.OrderResponseDto;
import com.innowise.orderservice.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {

    Order toEntity(CreateOrderRequestDto request);

    @Mapping(source = "orderItems", target = "items")
    OrderResponseDto toResponse(Order order);

    @Mapping(target = "orderItems", ignore = true)
    void updateEntity(UpdateOrderRequestDto request, @MappingTarget Order order);
}