package com.innowise.orderservice.item.service.impl;

import com.innowise.orderservice.item.dto.request.CreateItemRequestDto;
import com.innowise.orderservice.item.dto.request.UpdateItemRequestDto;
import com.innowise.orderservice.item.dto.response.ItemResponseDto;
import com.innowise.orderservice.item.entity.Item;
import com.innowise.orderservice.item.exception.ItemNotFoundException;
import com.innowise.orderservice.item.mapper.ItemMapper;
import com.innowise.orderservice.item.repository.ItemRepository;
import com.innowise.orderservice.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemResponseDto create(CreateItemRequestDto request) {
        Item item = itemMapper.toEntity(request);

        return itemMapper.toResponse(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto getById(UUID id) {
        return itemMapper.toResponse(getItemEntity(id));
    }

    @Override
    public List<ItemResponseDto> getAll() {
        return itemRepository.findAll().stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ItemResponseDto update(UUID id, UpdateItemRequestDto request) {
        Item item = getItemEntity(id);

        itemMapper.updateEntity(request, item);

        return itemMapper.toResponse(itemRepository.save(item));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        itemRepository.delete(getItemEntity(id));
    }

    private Item getItemEntity(UUID id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }
}
