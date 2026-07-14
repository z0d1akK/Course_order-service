package com.innowise.orderservice.order.repository;

import com.innowise.orderservice.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    @Modifying
    @Query("""
            UPDATE Order o
              SET o.deleted = true,
                  o.updatedAt = :updatedAt
              WHERE o.id = :id
            """)
    void softDeleteById(@Param("id") UUID id, @Param("updatedAt") LocalDateTime updatedAt);

    Page<Order> findAllByUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}