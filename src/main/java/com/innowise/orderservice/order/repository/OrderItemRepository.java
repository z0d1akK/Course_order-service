package com.innowise.orderservice.order.repository;

import com.innowise.orderservice.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    @Query("SELECT DISTINCT oi.order.id FROM OrderItem oi WHERE oi.item.id = :itemId")
    List<UUID> findOrderIdsByItemId(@Param("itemId") UUID itemId);

    boolean existsByItemId(UUID itemId);
}