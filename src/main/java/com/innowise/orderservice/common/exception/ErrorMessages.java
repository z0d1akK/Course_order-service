package com.innowise.orderservice.common.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {

    public static final String ORDER_NOT_FOUND = "Order not found with id: %s";

    public static final String ITEM_NOT_FOUND = "Item not found with id: %s";

    public static final String ITEM_ALREADY_EXISTS = "Item already exists";

    public static final String ITEM_IN_USE = "Item with id %s is used in orders: %s";

    public static final String USER_NOT_FOUND = "User not found";

    public static final String ACCESS_DENIED = "Access denied";

    public static final String UNAUTHORIZED = "Unauthorized";

    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";

    public static final String USER_SERVICE_UNAVAILABLE = "User service is unavailable";
}