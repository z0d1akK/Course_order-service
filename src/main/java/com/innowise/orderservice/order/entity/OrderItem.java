package com.innowise.orderservice.order.entity;

import com.innowise.orderservice.common.entity.BaseEntity;
import com.innowise.orderservice.item.entity.Item;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private Integer quantity;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrderItem orderItem = (OrderItem) o;

        return getId() != null && Objects.equals(getId(), orderItem.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}