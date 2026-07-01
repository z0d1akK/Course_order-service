package com.innowise.orderservice.item.service;

import com.innowise.orderservice.item.dto.request.CreateItemRequestDto;
import com.innowise.orderservice.item.dto.request.UpdateItemRequestDto;
import com.innowise.orderservice.item.dto.response.ItemResponseDto;
import com.innowise.orderservice.item.entity.Item;
import com.innowise.orderservice.item.exception.ItemAlreadyExistsException;
import com.innowise.orderservice.item.exception.ItemInUseException;
import com.innowise.orderservice.item.exception.ItemNotFoundException;
import com.innowise.orderservice.item.mapper.ItemMapper;
import com.innowise.orderservice.item.repository.ItemRepository;
import com.innowise.orderservice.item.service.impl.ItemServiceImpl;
import com.innowise.orderservice.order.repository.OrderItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.innowise.orderservice.item.testclasses.ItemTestDataFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    @DisplayName("Should create item successfully")
    void create_ShouldReturnCreatedItem() {
        CreateItemRequestDto request = createItemRequest();
        Item item = createItem();
        ItemResponseDto response = createItemResponse();

        when(itemRepository.existsByName(request.getName())).thenReturn(false);
        when(itemMapper.toEntity(request)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(response);

        ItemResponseDto result = itemService.create(request);

        assertThat(result).isEqualTo(response);

        verify(itemRepository).existsByName(request.getName());
        verify(itemMapper).toEntity(request);
        verify(itemRepository).save(item);
        verify(itemMapper).toResponse(item);
    }

    @Test
    @DisplayName("Should throw ItemAlreadyExistsException when item name exists")
    void create_WhenNameExists_ShouldThrowException() {
        CreateItemRequestDto request = createItemRequest();

        when(itemRepository.existsByName(request.getName())).thenReturn(true);

        assertThatThrownBy(() -> itemService.create(request))
                .isInstanceOf(ItemAlreadyExistsException.class);

        verify(itemRepository).existsByName(request.getName());
        verify(itemMapper, never()).toEntity(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return item when item exists")
    void getById_WhenItemExists_ShouldReturnItem() {
        UUID itemId = UUID.randomUUID();
        Item item = createItem(itemId);
        ItemResponseDto response = createItemResponse(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toResponse(item)).thenReturn(response);

        ItemResponseDto result = itemService.getById(itemId);

        assertThat(result).isEqualTo(response);

        verify(itemRepository).findById(itemId);
        verify(itemMapper).toResponse(item);
    }

    @Test
    @DisplayName("Should throw ItemNotFoundException when item does not exist")
    void getById_WhenItemNotExists_ShouldThrowException() {
        UUID itemId = UUID.randomUUID();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getById(itemId))
                .isInstanceOf(ItemNotFoundException.class);

        verify(itemRepository).findById(itemId);
    }

    @Test
    @DisplayName("Should return all items")
    void getAll_ShouldReturnAllItems() {
        Item firstItem = createItem(UUID.randomUUID());
        Item secondItem = createUpdatedItem(UUID.randomUUID());
        ItemResponseDto firstResponse = createItemResponse(firstItem.getId());
        ItemResponseDto secondResponse = createUpdatedItemResponse(secondItem.getId());

        when(itemRepository.findAll()).thenReturn(List.of(firstItem, secondItem));
        when(itemMapper.toResponse(firstItem)).thenReturn(firstResponse);
        when(itemMapper.toResponse(secondItem)).thenReturn(secondResponse);

        List<ItemResponseDto> result = itemService.getAll();

        assertThat(result)
                .hasSize(2)
                .containsExactly(firstResponse, secondResponse);

        verify(itemRepository).findAll();
    }

    @Test
    @DisplayName("Should update item successfully when item exists")
    void update_WhenItemExists_ShouldReturnUpdatedItem() {
        UUID itemId = UUID.randomUUID();
        UpdateItemRequestDto request = updateItemRequest();
        Item item = createItem(itemId);
        ItemResponseDto response = createUpdatedItemResponse(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(response);

        ItemResponseDto result = itemService.update(itemId, request);

        assertThat(result).isEqualTo(response);

        verify(itemRepository).findById(itemId);
        verify(itemMapper).updateEntity(request, item);
        verify(itemRepository).save(item);
        verify(itemMapper).toResponse(item);
    }

    @Test
    @DisplayName("Should throw ItemNotFoundException when updating non-existing item")
    void update_WhenItemNotExists_ShouldThrowException() {
        UUID itemId = UUID.randomUUID();
        UpdateItemRequestDto request = updateItemRequest();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(itemId, request))
                .isInstanceOf(ItemNotFoundException.class);

        verify(itemRepository).findById(itemId);
        verify(itemMapper, never()).updateEntity(any(), any());
    }

    @Test
    @DisplayName("Should delete item successfully when item exists and not used in orders")
    void delete_WhenItemExistsAndNotUsed_ShouldDeleteItem() {
        UUID itemId = UUID.randomUUID();
        Item item = createItem(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderItemRepository.existsByItemId(itemId)).thenReturn(false);

        itemService.delete(itemId);

        verify(itemRepository).findById(itemId);
        verify(orderItemRepository).existsByItemId(itemId);
        verify(orderItemRepository, never()).findOrderIdsByItemId(any());
        verify(itemRepository).delete(item);
    }

    @Test
    @DisplayName("Should throw ItemInUseException when item is used in orders")
    void delete_WhenItemIsUsed_ShouldThrowException() {
        UUID itemId = UUID.randomUUID();
        Item item = createItem(itemId);
        UUID orderId1 = UUID.randomUUID();
        UUID orderId2 = UUID.randomUUID();
        List<UUID> orderIds = List.of(orderId1, orderId2);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderItemRepository.existsByItemId(itemId)).thenReturn(true);
        when(orderItemRepository.findOrderIdsByItemId(itemId)).thenReturn(orderIds);

        assertThatThrownBy(() -> itemService.delete(itemId))
                .isInstanceOf(ItemInUseException.class)
                .extracting("orderIds")
                .isEqualTo(orderIds);

        verify(itemRepository).findById(itemId);
        verify(orderItemRepository).existsByItemId(itemId);
        verify(orderItemRepository).findOrderIdsByItemId(itemId);
        verify(itemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw ItemNotFoundException when deleting non-existing item")
    void delete_WhenItemNotExists_ShouldThrowException() {
        UUID itemId = UUID.randomUUID();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.delete(itemId))
                .isInstanceOf(ItemNotFoundException.class);

        verify(itemRepository).findById(itemId);
        verify(orderItemRepository, never()).existsByItemId(any());
        verify(itemRepository, never()).delete(any());
    }
}