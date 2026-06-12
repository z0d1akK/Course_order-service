package com.innowise.orderservice.item.controller;

import com.innowise.orderservice.common.dto.response.ApiErrorResponse;
import com.innowise.orderservice.common.dto.response.ValidationErrorResponse;
import com.innowise.orderservice.item.dto.request.CreateItemRequestDto;
import com.innowise.orderservice.item.dto.request.UpdateItemRequestDto;
import com.innowise.orderservice.item.dto.response.ItemResponseDto;
import com.innowise.orderservice.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    /**
     * Creates a new item available for ordering.
     * Only users with ADMIN role can create items.
     *
     * @param request the create item request DTO containing item details
     * @return ResponseEntity containing the created item response DTO with HTTP status 201 (Created)
     * @throws com.innowise.orderservice.common.exception.BusinessException if request validation fails
     * @throws org.springframework.security.access.AccessDeniedException if current user doesn't have ADMIN authority
     */
    @Operation(summary = "Create item", description = "Creates a new item available for ordering")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Item successfully created")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized user detected",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Access denied for current user",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ItemResponseDto> create(@Valid @RequestBody CreateItemRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(request));
    }

    /**
     * Retrieves an item by its unique identifier.
     * Available to all authenticated and unauthenticated users.
     *
     * @param id the UUID of the item to retrieve
     * @return ResponseEntity containing the item response DTO with HTTP status 200 (OK)
     * @throws com.innowise.orderservice.common.exception.ResourceNotFoundException if item with given id doesn't exist
     */
    @Operation(summary = "Get item by id", description = "Returns item information")
    @ApiResponse(responseCode = "200", description = "Item found")
    @ApiResponse(responseCode = "404", description = "Item not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    /**
     * Retrieves all available items.
     * Returns a complete list of items without pagination.
     *
     * @return ResponseEntity containing a list of all item response DTOs with HTTP status 200 (OK)
     */
    @Operation(summary = "Get all items", description = "Returns all available items")
    @ApiResponse(responseCode = "200", description = "Items successfully retrieved")
    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAll() {
        return ResponseEntity.ok(itemService.getAll());
    }

    /**
     * Updates an existing item by its identifier.
     * Only users with ADMIN role can update items.
     *
     * @param id the UUID of the item to update
     * @param request the update item request DTO containing updated fields
     * @return ResponseEntity containing the updated item response DTO with HTTP status 200 (OK)
     * @throws com.innowise.orderservice.common.exception.ResourceNotFoundException if item with given id doesn't exist
     * @throws com.innowise.orderservice.common.exception.BusinessException if request validation fails
     * @throws org.springframework.security.access.AccessDeniedException if current user doesn't have ADMIN authority
     */
    @Operation(summary = "Update item", description = "Updates existing item")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Item successfully updated")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized user detected",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Access denied for current user",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Item not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponseDto> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateItemRequestDto request) {
        return ResponseEntity.ok(itemService.update(id, request));
    }

    /**
     * Deletes an item by its unique identifier.
     * Only users with ADMIN role can delete items.
     *
     * @param id the UUID of the item to delete
     * @return ResponseEntity with HTTP status 204 (No Content) on successful deletion
     * @throws com.innowise.orderservice.common.exception.ResourceNotFoundException if item with given id doesn't exist
     * @throws org.springframework.security.access.AccessDeniedException if current user doesn't have ADMIN authority
     */
    @Operation(summary = "Delete item", description = "Deletes item by identifier")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Item successfully deleted")
    @ApiResponse(responseCode = "401", description = "Unauthorized user detected",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Access denied for current user",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Item not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
