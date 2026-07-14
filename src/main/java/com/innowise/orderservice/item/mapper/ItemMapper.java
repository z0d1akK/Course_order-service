package com.innowise.orderservice.item.mapper;

import com.innowise.orderservice.config.MapStructConfig;
import com.innowise.orderservice.item.dto.request.CreateItemRequestDto;
import com.innowise.orderservice.item.dto.request.UpdateItemRequestDto;
import com.innowise.orderservice.item.dto.response.ItemResponseDto;
import com.innowise.orderservice.item.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface ItemMapper {

    Item toEntity(CreateItemRequestDto request);

    ItemResponseDto toResponse(Item item);

    void updateEntity(UpdateItemRequestDto request, @MappingTarget Item item);
}