package com.techpalle.controller;



import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import com.techpalle.dto.response.PaginatedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techpalle.dto.request.AllocationRequestDTO;
import com.techpalle.dto.request.AutoAllocationRequestDTO;
import com.techpalle.dto.response.AllocationResponseDTO;
import com.techpalle.dto.response.ApiResponse;
import com.techpalle.service.AllocationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/allocations")
@RequiredArgsConstructor
@Slf4j

public class AllocationController {

	 private final AllocationService allocationService;

	    @PostMapping
	    public ResponseEntity<ApiResponse<AllocationResponseDTO>> allocateProduct(@Valid @RequestBody 
	    		AllocationRequestDTO requestDTO,
                @RequestHeader(
	                    value = "X-User-ID",
	                    defaultValue = "SYSTEM")
	            String userId) {

	        AllocationResponseDTO response =
	                allocationService.allocateProductToWarehouse(
	                        requestDTO,
	                        userId);

	        return ResponseEntity.status(HttpStatus.CREATED)
	                .body(ApiResponse.success(
	                        response,
	                        "Product allocated successfully",
	                        HttpStatus.CREATED.value()));
	    }

	    @PostMapping("/auto-allocate")
	    public ResponseEntity<ApiResponse<AllocationResponseDTO>>
	          autoAllocateProduct( @Valid @RequestBody AutoAllocationRequestDTO requestDTO,
                @RequestHeader(
	                    value = "X-User-ID",
	                    defaultValue = "SYSTEM")
	            String userId) {

	        AllocationResponseDTO response = allocationService.autoAllocateProduct(requestDTO, userId);

	        return ResponseEntity.status(HttpStatus.CREATED)
	                .body(ApiResponse.success(
	                        response,
	                        "Product auto allocated successfully",
	                        HttpStatus.CREATED.value()));
	    }

	    @GetMapping("/{id}")
	    public ResponseEntity<ApiResponse<AllocationResponseDTO>>
	              getAllocation(@PathVariable Long id) {

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        allocationService.getAllocationById(id),
	                        "Allocation retrieved successfully"));
	    }

	    @GetMapping("/request/{requestId}")
	    public ResponseEntity<ApiResponse<AllocationResponseDTO>>
	    getByRequestId( @PathVariable String requestId) {

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        allocationService.getByRequestId(requestId),
	                        "Allocation retrieved successfully"));
	    }

	    @GetMapping
	    public ResponseEntity<ApiResponse<PaginatedResponse<AllocationResponseDTO>>>
	    getAllAllocations(

	            @RequestParam(defaultValue = "0")
	            int page,

	            @RequestParam(defaultValue = "10")
	            int size,

	            @RequestParam(defaultValue = "allocatedAt")
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
	                        allocationService.getAllAllocations(pageable),
	                        "Allocations retrieved successfully"));
	    }

	    @GetMapping("/product/{productId}")
	    public ResponseEntity<ApiResponse<PaginatedResponse<AllocationResponseDTO>>>
	    getAllocationsByProduct(

	            @PathVariable Long productId,

	            @RequestParam(defaultValue = "0")
	            int page,

	            @RequestParam(defaultValue = "10")
	            int size) {

	        validatePagination(page, size);

	        Pageable pageable =
	                PageRequest.of(
	                        page,
	                        size,
	                        Sort.by(
	                                Sort.Direction.DESC,
	                                "allocatedAt"));

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        allocationService.getAllocationsByProduct(
	                                productId,
	                                pageable),
	                        "Allocations retrieved successfully"));
	    }

	    @GetMapping("/warehouse/{warehouseId}")
	    public ResponseEntity<ApiResponse<PaginatedResponse<AllocationResponseDTO>>>
	    getAllocationsByWarehouse(

	            @PathVariable Long warehouseId,

	            @RequestParam(defaultValue = "0")
	            int page,

	            @RequestParam(defaultValue = "10")
	            int size) {

	        validatePagination(page, size);

	        Pageable pageable =
	                PageRequest.of(
	                        page,
	                        size,
	                        Sort.by(
	                                Sort.Direction.DESC,
	                                "allocatedAt"));

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        allocationService.getAllocationsByWarehouse(
	                                warehouseId,
	                                pageable),
	                        "Allocations retrieved successfully"));
	    }

	    @GetMapping("/search/product-date-range")
	    public ResponseEntity<ApiResponse<PaginatedResponse<AllocationResponseDTO>>>
	    getAllocationsByProductAndDateRange(

	            @RequestParam Long productId,

	            @RequestParam
	            @DateTimeFormat(
	                    iso = DateTimeFormat.ISO.DATE_TIME)
	            LocalDateTime startDate,

	            @RequestParam
	            @DateTimeFormat(
	                    iso = DateTimeFormat.ISO.DATE_TIME)
	            LocalDateTime endDate,

	            @RequestParam(defaultValue = "0")
	            int page,

	            @RequestParam(defaultValue = "10")
	            int size) {

	        Pageable pageable =
	                PageRequest.of(page, size);

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        allocationService
	                                .getAllocationsByProductAndDateRange(
	                                        productId,
	                                        startDate,
	                                        endDate,
	                                        pageable),
	                        "Allocations retrieved successfully"));
	    }

	    @GetMapping("/search/warehouse-date-range")
	    public ResponseEntity<ApiResponse<PaginatedResponse<AllocationResponseDTO>>>
	    getAllocationsByWarehouseAndDateRange(

	            @RequestParam Long warehouseId,

	            @RequestParam
	            @DateTimeFormat(
	                    iso = DateTimeFormat.ISO.DATE_TIME)
	            LocalDateTime startDate,

	            @RequestParam
	            @DateTimeFormat(
	                    iso = DateTimeFormat.ISO.DATE_TIME)
	            LocalDateTime endDate,

	            @RequestParam(defaultValue = "0")
	            int page,

	            @RequestParam(defaultValue = "10")
	            int size) {

	        Pageable pageable =
	                PageRequest.of(page, size);

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        allocationService
	                                .getAllocationsByWarehouseAndDateRange(
	                                        warehouseId,
	                                        startDate,
	                                        endDate,
	                                        pageable),
	                        "Allocations retrieved successfully"));
	    }

	    @GetMapping("/search/date-range")
	    public ResponseEntity<ApiResponse<PaginatedResponse<AllocationResponseDTO>>>
	    getAllocationsByDateRange(

	            @RequestParam
	            @DateTimeFormat(
	                    iso = DateTimeFormat.ISO.DATE_TIME)
	            LocalDateTime startDate,

	            @RequestParam
	            @DateTimeFormat(
	                    iso = DateTimeFormat.ISO.DATE_TIME)
	            LocalDateTime endDate,

	            @RequestParam(defaultValue = "0")
	            int page,

	            @RequestParam(defaultValue = "10")
	            int size) {

	        Pageable pageable =
	                PageRequest.of(page, size);

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        allocationService.getAllocationsByDateRange(
	                                startDate,
	                                endDate,
	                                pageable),
	                        "Allocations retrieved successfully"));
	    }

	    @PutMapping("/{id}/confirm")
	    public ResponseEntity<ApiResponse<AllocationResponseDTO>>
	    confirmAllocation(@PathVariable Long id) {

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        allocationService.confirmAllocation(id),
	                        "Allocation confirmed successfully"));
	    }

	    @PutMapping("/{id}/cancel")
	    public ResponseEntity<ApiResponse<AllocationResponseDTO>>
	    cancelAllocation(@PathVariable Long id) {

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        allocationService.cancelAllocation(id),
	                        "Allocation cancelled successfully"));
	    }

	    private void validatePagination(int page, int size) {

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
