package com.techpalle.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techpalle.dto.response.ApiResponse;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.WarehouseInventoryResponseDTO;
import com.techpalle.service.WarehouseInventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/inventory")
@Slf4j
@RequiredArgsConstructor
public class WarehouseInventoryController {

private final WarehouseInventoryService inventoryService;

    @GetMapping(
            "/warehouse/{warehouseId}/product/{productId}")
    public ResponseEntity<
            ApiResponse<WarehouseInventoryResponseDTO>>
    getInventory(
            @PathVariable Long warehouseId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        inventoryService.getInventory(
                                warehouseId,
                                productId),
                        "Inventory retrieved successfully"));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<
            ApiResponse<
                    PaginatedResponse<
                            WarehouseInventoryResponseDTO>>>
    getWarehouseInventory(

            @PathVariable Long warehouseId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "updatedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        validatePagination(page, size);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                direction.equalsIgnoreCase("DESC")
                                        ? Sort.Direction.DESC
                                        : Sort.Direction.ASC,
                                sortBy));

        return ResponseEntity.ok(
                ApiResponse.success(
                        inventoryService.getWarehouseInventory(
                                warehouseId,
                                pageable),
                        "Warehouse inventory retrieved successfully"));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<
            ApiResponse<
                    PaginatedResponse<
                            WarehouseInventoryResponseDTO>>>
    getProductInventory(

            @PathVariable Long productId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "updatedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        validatePagination(page, size);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                direction.equalsIgnoreCase("DESC")
                                        ? Sort.Direction.DESC
                                        : Sort.Direction.ASC,
                                sortBy));

        return ResponseEntity.ok(
                ApiResponse.success(
                        inventoryService.getProductInventory(
                                productId,
                                pageable),
                        "Product inventory retrieved successfully"));
    }

    @GetMapping(
            "/product/{productId}/available-warehouses")
    public ResponseEntity<
            ApiResponse<
                    List<WarehouseInventoryResponseDTO>>>
    getAvailableWarehouses(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        inventoryService
                                .getAvailableWarehouses(
                                        productId),
                        "Available warehouses retrieved successfully"));
    }

    @GetMapping("/product/{productId}/total-stock")
    public ResponseEntity<ApiResponse<Long>>
    getTotalAvailableStock(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        inventoryService
                                .getTotalAvailableStock(
                                        productId),
                        "Total stock retrieved successfully"));
    }

    @PutMapping(
            "/warehouse/{warehouseId}/product/{productId}")
    public ResponseEntity<
            ApiResponse<WarehouseInventoryResponseDTO>>
    setInventory(

            @PathVariable Long warehouseId,

            @PathVariable Long productId,

            @RequestParam Long quantity) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        inventoryService.setInventory(
                                warehouseId,
                                productId,
                                quantity),
                        "Inventory updated successfully"));
    }

    @PatchMapping(
            "/warehouse/{warehouseId}/product/{productId}")
    public ResponseEntity<
            ApiResponse<WarehouseInventoryResponseDTO>>
    adjustInventory(

            @PathVariable Long warehouseId,

            @PathVariable Long productId,

            @RequestParam Long quantityChange) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        inventoryService.adjustInventory(
                                warehouseId,
                                productId,
                                quantityChange),
                        "Inventory adjusted successfully"));
    }

    @PostMapping(
            "/warehouse/{warehouseId}/product/{productId}/initialize")
    public ResponseEntity<
            ApiResponse<WarehouseInventoryResponseDTO>>
    initializeInventory(

            @PathVariable Long warehouseId,

            @PathVariable Long productId,

            @RequestParam Long quantity) {

        WarehouseInventoryResponseDTO response =
                inventoryService.initializeInventory(
                        warehouseId,
                        productId,
                        quantity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                response,
                                "Inventory initialized successfully",
                                HttpStatus.CREATED.value()));
    }

    private void validatePagination(
            int page,
            int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative");
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "Size must be greater than zero");
        }
    }




 
    

}
