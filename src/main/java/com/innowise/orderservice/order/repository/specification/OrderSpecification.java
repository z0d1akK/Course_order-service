package com.innowise.orderservice.order.repository.specification;

import com.innowise.orderservice.order.entity.Order;
import com.innowise.orderservice.order.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Set;

public final class OrderSpecification {

    private OrderSpecification() { }

    public static Specification<Order> createdFrom(LocalDateTime createdFrom) {
        return (root, query, criteriaBuilder) -> {
            if (createdFrom == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdFrom);
        };
    }

    public static Specification<Order> createdTo(LocalDateTime createdTo) {
        return (root, query, criteriaBuilder) -> {
            if (createdTo == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdTo);
        };
    }

    public static Specification<Order> hasStatuses(Set<OrderStatus> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return root.get("status").in(statuses);
        };
    }

    public static Specification<Order> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));
    }
}