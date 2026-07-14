package com.innowise.orderservice.item.repository;

import com.innowise.orderservice.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    boolean existsByName(String name);
}