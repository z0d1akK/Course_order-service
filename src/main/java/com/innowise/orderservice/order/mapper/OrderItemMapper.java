package com.innowise.orderservice.order.mapper;

import com.innowise.orderservice.config.MapStructConfig;
import com.innowise.orderservice.item.mapper.ItemMapper;
import com.innowise.orderservice.order.dto.response.OrderItemResponseDto;
import com.innowise.orderservice.order.entity.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class, uses = ItemMapper.class)
public interface OrderItemMapper {

    OrderItemResponseDto toResponse(OrderItem entity);

    List<OrderItemResponseDto> toResponseList(List<OrderItem> entities);
}