package com.innowise.orderservice.item.exception;

import com.innowise.orderservice.common.exception.BusinessException;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

import static com.innowise.orderservice.common.exception.ErrorMessages.ITEM_IN_USE;

@Getter
public class ItemInUseException extends BusinessException {

    private final List<UUID> orderIds;

    public ItemInUseException(UUID itemId, List<UUID> orderIds) {
        super(String.format(ITEM_IN_USE, itemId, orderIds));
        this.orderIds = orderIds;
    }
}