package com.innowise.orderservice.item.service;

import com.innowise.orderservice.item.dto.request.CreateItemRequestDto;
import com.innowise.orderservice.item.dto.request.UpdateItemRequestDto;
import com.innowise.orderservice.item.dto.response.ItemResponseDto;

import java.util.List;
import java.util.UUID;

public interface ItemService {

    /**
     * Creates a new item.
     *
     * @param request request object containing item data
     * @return created item response
     */
    ItemResponseDto create(CreateItemRequestDto request);

    /**
     * Returns item by identifier.
     *
     * @param id item identifier
     * @return item response
     */
    ItemResponseDto getById(UUID id);

    /**
     * Returns all available items.
     *
     * @return list of item responses
     */
    List<ItemResponseDto> getAll();

    /**
     * Updates existing item.
     *
     * @param id item identifier
     * @param request request object containing updated item data
     * @return updated item response
     */
    ItemResponseDto update(UUID id, UpdateItemRequestDto request);

    /**
     * Deletes item by identifier.
     *
     * @param id item identifier
     */
    void delete(UUID id);
}