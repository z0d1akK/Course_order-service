package com.innowise.orderservice.order.controller;

import com.innowise.orderservice.common.dto.response.ApiErrorResponse;
import com.innowise.orderservice.common.dto.response.ValidationErrorResponse;
import com.innowise.orderservice.order.dto.request.CreateOrderRequestDto;
import com.innowise.orderservice.order.dto.request.OrderFilterRequestDto;
import com.innowise.orderservice.order.dto.request.UpdateOrderRequestDto;
import com.innowise.orderservice.order.dto.response.OrderDetailsResponseDto;
import com.innowise.orderservice.order.service.OrderService;
import com.innowise.orderservice.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order for the current user.
     * The order is associated with the user ID provided in the request.
     * Access is restricted to the order owner or ADMIN users.
     *
     * @param request the create order request DTO containing order details and user ID
     * @return ResponseEntity containing the created order details DTO with HTTP status 201 (Created)
     * @throws com.innowise.orderservice.common.exception.BusinessException if request validation fails
     * @throws org.springframework.security.access.AccessDeniedException if user is not owner or ADMIN
     */
    @Operation(summary = "Create order", description = "Creates a new order for the current user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Order successfully created")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized user detected",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Access denied for current user",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PreAuthorize("@ownershipService.isOwnerOrAdmin(#request.userId)")
    @PostMapping
    public ResponseEntity<OrderDetailsResponseDto> create(@Valid @RequestBody CreateOrderRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    /**
     * Retrieves an order by its unique identifier with full user information.
     * Access is restricted to the order owner or ADMIN users.
     *
     * @param id the UUID of the order to retrieve
     * @return ResponseEntity containing the order details DTO with HTTP status 200 (OK)
     * @throws com.innowise.orderservice.common.exception.ResourceNotFoundException if order with given id doesn't exist
     * @throws org.springframework.security.access.AccessDeniedException if user is not owner or ADMIN
     */
    @Operation(summary = "Get order by id", description = "Returns order with user information")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "401", description = "Unauthorized user detected",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Access denied for current user",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PreAuthorize("@orderOwnershipService.isOwnerOrAdmin(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    /**
     * Retrieves all orders with pagination and filtering capabilities.
     * Available only to users with ADMIN role.
     *
     * @param filter the filter DTO containing order filtering criteria
     * @param pageable the pagination information (page number, size, sorting)
     * @return ResponseEntity containing a paginated list of order details DTOs with HTTP status 200 (OK)
     * @throws org.springframework.security.access.AccessDeniedException if current user doesn't have ADMIN authority
     */
    @Operation(summary = "Get all orders", description = "Returns paginated list of orders with filtering")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Orders successfully retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized user detected",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Access denied for current user",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<OrderDetailsResponseDto>> getAll(OrderFilterRequestDto filter, Pageable pageable) {
        return ResponseEntity.ok(orderService.getAll(filter, pageable));
    }

    /**
     * Retrieves all orders belonging to the currently authenticated user.
     * Returns orders with pagination support.
     *
     * @param pageable the pagination information (page number, size, sorting)
     * @return ResponseEntity containing a paginated list of the current user's order details DTOs with HTTP status 200 (OK)
     */
    @Operation(summary = "Get current user orders", description = "Returns paginated list of current user orders")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Orders successfully retrieved")
    @GetMapping("/my")
    public ResponseEntity<Page<OrderDetailsResponseDto>> getMyOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getByUserId(SecurityUtils.getCurrentUserId(), pageable));
    }

    /**
     * Updates an existing order by its identifier.
     * Only users with ADMIN role can update orders.
     *
     * @param id the UUID of the order to update
     * @param request the update order request DTO containing updated fields
     * @return ResponseEntity containing the updated order details DTO with HTTP status 200 (OK)
     * @throws com.innowise.orderservice.common.exception.ResourceNotFoundException if order with given id doesn't exist
     * @throws com.innowise.orderservice.common.exception.BusinessException if request validation fails
     * @throws org.springframework.security.access.AccessDeniedException if current user doesn't have ADMIN authority
     */
    @Operation(summary = "Update order", description = "Updates existing order")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Order successfully updated")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized user detected",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Access denied for current user",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<OrderDetailsResponseDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateOrderRequestDto request) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    /**
     * Performs a soft delete of an order by its identifier.
     * The order is marked as deleted but remains in the database for audit purposes.
     * Only users with ADMIN role can delete orders.
     *
     * @param id the UUID of the order to delete
     * @return ResponseEntity with HTTP status 204 (No Content) on successful deletion
     * @throws com.innowise.orderservice.common.exception.ResourceNotFoundException if order with given id doesn't exist
     * @throws org.springframework.security.access.AccessDeniedException if current user doesn't have ADMIN authority
     */
    @Operation(summary = "Delete order", description = "Performs soft delete of an order")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Order successfully deleted")
    @ApiResponse(responseCode = "401", description = "Unauthorized user detected",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Access denied for current user",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}