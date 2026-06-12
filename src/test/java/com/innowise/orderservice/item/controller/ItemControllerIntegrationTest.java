package com.innowise.orderservice.item.controller;

import com.innowise.orderservice.common.AbstractIntegrationTest;
import com.innowise.orderservice.common.annotation.WithMockCustomUser;
import com.innowise.orderservice.item.dto.request.CreateItemRequestDto;
import com.innowise.orderservice.item.dto.request.UpdateItemRequestDto;
import com.innowise.orderservice.item.entity.Item;
import com.innowise.orderservice.item.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static com.innowise.orderservice.item.testclasses.ItemTestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create item")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void create_ShouldCreateItem() throws Exception {
        CreateItemRequestDto request = createItemRequest();

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("IPhone 17"))
                .andExpect(jsonPath("$.price").value(3000));

        assertThat(itemRepository.findAll()).hasSize(1);

        Item item = itemRepository.findAll().getFirst();

        assertThat(item.getName()).isEqualTo("IPhone 17");
        assertThat(item.getPrice()).isEqualByComparingTo("3000");
    }

    @Test
    @DisplayName("Should return item by id")
    void getById_ShouldReturnItem() throws Exception {
        Item item = itemRepository.save(createItem());

        mockMvc.perform(get("/api/items/{id}", item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId().toString()))
                .andExpect(jsonPath("$.name").value("IPhone 17"))
                .andExpect(jsonPath("$.price").value(3000));
    }

    @Test
    @DisplayName("Should return all items")
    void getAll_ShouldReturnItems() throws Exception {
        itemRepository.save(createItem());

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Should update item")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void update_ShouldUpdateItem() throws Exception {
        Item item = itemRepository.save(createItem());

        UpdateItemRequestDto request = updateItemRequest();

        mockMvc.perform(patch("/api/items/{id}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId().toString()))
                .andExpect(jsonPath("$.name").value("IPhone 17 Pro"))
                .andExpect(jsonPath("$.price").value(3200));

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();

        assertThat(updatedItem.getName()).isEqualTo("IPhone 17 Pro");
        assertThat(updatedItem.getPrice()).isEqualByComparingTo("3200");
    }

    @Test
    @DisplayName("Should delete item")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void delete_ShouldDeleteItem() throws Exception {
        Item item = itemRepository.save(createItem());

        mockMvc.perform(delete("/api/items/{id}", item.getId()))
                .andExpect(status().isNoContent());

        assertThat(itemRepository.findById(item.getId()))
                .isEmpty();
    }

    @Test
    @DisplayName("Should return 403 when user creates item")
    @WithMockCustomUser()
    void create_WhenUserRole_ShouldReturn403() throws Exception {
        CreateItemRequestDto request = createItemRequest();

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 403 when user update item")
    @WithMockCustomUser()
    void update_WhenUserRole_ShouldReturn403() throws Exception {
        Item item = itemRepository.save(createItem());

        UpdateItemRequestDto request = updateItemRequest();

        mockMvc.perform(patch("/api/items/{id}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 403 when user delete item")
    @WithMockCustomUser()
    void delete_WhenUserRole_ShouldReturn403() throws Exception {
        Item item = itemRepository.save(createItem());

        mockMvc.perform(delete("/api/items/{id}", item.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 when item not found")
    void getById_WhenItemNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/items/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}