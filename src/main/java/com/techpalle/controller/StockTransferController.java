package com.techpalle.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.techpalle.dto.request.StockTransferRequestDTO;
import com.techpalle.dto.response.ApiResponse;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.StockTransferResponseDTO;
import com.techpalle.service.StockTransferService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/stock-transfers")
@RequiredArgsConstructor
@Slf4j

public class StockTransferController {

   private final StockTransferService stockTransferService;

    @PostMapping
    
    public ResponseEntity<ApiResponse<StockTransferResponseDTO>>
    initiateTransfer(

            @Valid
            @RequestBody
            StockTransferRequestDTO requestDTO,

            @RequestHeader(
                    value = "X-User-ID",
                    defaultValue = "SYSTEM")
            String userId) {

        StockTransferResponseDTO response =
                stockTransferService.initiateTransfer(
                        requestDTO,
                        userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                response,
                                "Stock transfer created successfully",
                                HttpStatus.CREATED.value()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockTransferResponseDTO>>
    getTransfer(
            @PathVariable Long id) {

        StockTransferResponseDTO response =
                stockTransferService.getTransferById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Transfer retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<
            ApiResponse<
                    PaginatedResponse<StockTransferResponseDTO>>>
    getAllTransfers(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "transferDate")
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
                        stockTransferService.getAllTransfers(pageable),
                        "Transfers retrieved successfully"));
    }

    @GetMapping("/warehouse/from/{warehouseId}")
    public ResponseEntity<
            ApiResponse<
                    PaginatedResponse<StockTransferResponseDTO>>>
    getTransfersFromWarehouse(

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
                                "transferDate"));

        return ResponseEntity.ok(
                ApiResponse.success(
                        stockTransferService
                                .getTransfersFromWarehouse(
                                        warehouseId,
                                        pageable),
                        "Transfers retrieved successfully"));
    }

    @GetMapping("/warehouse/to/{warehouseId}")
    public ResponseEntity<
            ApiResponse<
                    PaginatedResponse<StockTransferResponseDTO>>>
    getTransfersToWarehouse(

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
                                "transferDate"));

        return ResponseEntity.ok(
                ApiResponse.success(
                        stockTransferService
                                .getTransfersToWarehouse(
                                        warehouseId,
                                        pageable),
                        "Transfers retrieved successfully"));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<
            ApiResponse<
                    PaginatedResponse<StockTransferResponseDTO>>>
    getTransfersByProduct(

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
                                "transferDate"));

        return ResponseEntity.ok(
                ApiResponse.success(
                        stockTransferService
                                .getTransfersByProduct(
                                        productId,
                                        pageable),
                        "Transfers retrieved successfully"));
    }

    @GetMapping("/search/date-range")
    public ResponseEntity<
            ApiResponse<
                    PaginatedResponse<StockTransferResponseDTO>>>
    getTransfersByDateRange(

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

        validatePagination(page, size);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "transferDate"));

        return ResponseEntity.ok(
                ApiResponse.success(
                        stockTransferService
                                .getTransfersByDateRange(
                                        startDate,
                                        endDate,
                                        pageable),
                        "Transfers retrieved successfully"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<StockTransferResponseDTO>>
    completeTransfer(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        stockTransferService.completeTransfer(id),
                        "Transfer completed successfully"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<StockTransferResponseDTO>>
    cancelTransfer(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        stockTransferService.cancelTransfer(id),
                        "Transfer cancelled successfully"));
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
