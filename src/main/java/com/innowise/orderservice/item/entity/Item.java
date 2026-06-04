package com.innowise.orderservice.item.entity;

import com.innowise.orderservice.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Item extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return getId() != null && Objects.equals(getId(), item.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}