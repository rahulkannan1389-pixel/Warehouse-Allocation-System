package com.techpalle.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.techpalle.dto.request.WarehouseRequestDTO;
import com.techpalle.dto.response.ApiResponse;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.WarehouseResponseDTO;
import com.techpalle.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Slf4j
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseResponseDTO>>
       createWarehouse( @Valid @RequestBody WarehouseRequestDTO requestDTO) {

        log.info("Creating warehouse: {}", requestDTO.getName());

        WarehouseResponseDTO response =warehouseService.createWarehouse(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        response,
                        "Warehouse created successfully",
                        HttpStatus.CREATED.value()));
    }

    @GetMapping("/{id}")  
    public ResponseEntity<ApiResponse<WarehouseResponseDTO>>
    getWarehouse(@PathVariable Long id) {

        WarehouseResponseDTO response =warehouseService.getWarehouseById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Warehouse fetched successfully"));
    }

    @GetMapping
    public ResponseEntity< ApiResponse<PaginatedResponse<WarehouseResponseDTO>>>
         getAllWarehouses(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative");
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "Size must be greater than zero");
        }

        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("DESC")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of( page, size, Sort.by(sortDirection, sortBy));

        PaginatedResponse<WarehouseResponseDTO> response =warehouseService.getAllWarehouses(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Warehouses fetched successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponseDTO>>
        updateWarehouse( @PathVariable Long id, @Valid @RequestBody WarehouseRequestDTO requestDTO) {

        WarehouseResponseDTO response =warehouseService.updateWarehouse(id,requestDTO);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Warehouse updated successfully"));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<WarehouseResponseDTO>>activateWarehouse( @PathVariable Long id) {

        WarehouseResponseDTO response =warehouseService.activateWarehouse(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Warehouse activated successfully"));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<WarehouseResponseDTO>>
              deactivateWarehouse( @PathVariable Long id) {

        WarehouseResponseDTO response = warehouseService.deactivateWarehouse(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Warehouse deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>>
       deleteWarehouse(@PathVariable Long id) {

        warehouseService.deleteWarehouse(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Warehouse deleted successfully"));
    }
}
