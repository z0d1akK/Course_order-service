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

    @Operation(summary = "Get current user orders", description = "Returns paginated list of current user orders")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Orders successfully retrieved")
    @GetMapping("/my")
    public ResponseEntity<Page<OrderDetailsResponseDto>> getMyOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getByUserId(SecurityUtils.getCurrentUserId(), pageable));
    }

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