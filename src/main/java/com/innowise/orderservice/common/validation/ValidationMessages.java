package com.innowise.orderservice.common.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationMessages {

    public static final String ITEM_NAME_REQUIRED = "Item name is required";
    public static final String ITEM_NAME_SIZE = "Item name must contain from 1 to 255 characters";
    public static final String ITEM_PRICE_REQUIRED = "Item price is required";
    public static final String ITEM_PRICE_POSITIVE = "Item price must be positive";

    public static final String ORDER_USER_ID_REQUIRED = "User id is required";
    public static final String ORDER_STATUS_REQUIRED = "Order status is required";

    public static final String ORDER_ITEMS_REQUIRED = "Order must contain at least one item";
    public static final String ORDER_ITEM_ID_REQUIRED = "Item id is required";
    public static final String ORDER_ITEM_QUANTITY_REQUIRED = "Quantity is required";
    public static final String ORDER_ITEM_QUANTITY_POSITIVE = "Quantity must be greater than zero";
}