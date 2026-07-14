package com.innowise.orderservice.item.testclasses;

import com.innowise.orderservice.item.dto.request.CreateItemRequestDto;
import com.innowise.orderservice.item.dto.request.UpdateItemRequestDto;
import com.innowise.orderservice.item.dto.response.ItemResponseDto;
import com.innowise.orderservice.item.entity.Item;

import java.math.BigDecimal;
import java.util.UUID;

public final class ItemTestDataFactory {

    private ItemTestDataFactory() { }

    public static CreateItemRequestDto createItemRequest() {
        return CreateItemRequestDto.builder()
                .name("IPhone 17")
                .price(BigDecimal.valueOf(3000))
                .build();
    }

    public static UpdateItemRequestDto updateItemRequest() {
        return UpdateItemRequestDto.builder()
                .name("IPhone 17 Pro")
                .price(BigDecimal.valueOf(3200))
                .build();
    }

    public static Item createItem() {
        return Item.builder()
                .name("IPhone 17")
                .price(BigDecimal.valueOf(3000))
                .build();
    }

    public static Item createItem(UUID id) {
        Item item = createItem();
        item.setId(id);

        return item;
    }

    public static Item createUpdatedItem(UUID id) {
        return Item.builder()
                .id(id)
                .name("IPhone 17 Pro")
                .price(BigDecimal.valueOf(3200))
                .build();
    }

    public static ItemResponseDto createItemResponse() {
        return createItemResponse(UUID.randomUUID());
    }

    public static ItemResponseDto createItemResponse(UUID id) {
        return ItemResponseDto.builder()
                .id(id)
                .name("IPhone 17")
                .price(BigDecimal.valueOf(3000))
                .build();
    }

    public static ItemResponseDto createUpdatedItemResponse(UUID id) {
        return ItemResponseDto.builder()
                .id(id)
                .name("IPhone 17 Pro")
                .price(BigDecimal.valueOf(3200))
                .build();
    }
}