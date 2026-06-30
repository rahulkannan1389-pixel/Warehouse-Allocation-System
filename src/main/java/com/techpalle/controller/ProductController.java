package com.techpalle.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.techpalle.dto.request.ProductRequestDTO;
import com.techpalle.dto.response.ApiResponse;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.ProductResponseDTO;
import com.techpalle.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j

public class ProductController {

	 private final ProductService productService;

	    @PostMapping
	    public ResponseEntity<ApiResponse<ProductResponseDTO>>
	             createProduct( @Valid @RequestBody ProductRequestDTO requestDTO) {

	        log.info("Creating product with SKU: {}", requestDTO.getSku());

	        ProductResponseDTO response =productService.createProduct(requestDTO);

	        return ResponseEntity.status(HttpStatus.CREATED)
	                .body(
	                        ApiResponse.success(
	                                response,
	                                "Product created successfully",
	                                HttpStatus.CREATED.value()));
	    }

	    @GetMapping("/{id}")
	    public ResponseEntity<ApiResponse<ProductResponseDTO>>
	    getProduct( @PathVariable Long id) {

	        ProductResponseDTO response =productService.getProductById(id);

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        response,
	                        "Product retrieved successfully"));
	    }

	    @GetMapping
	    public ResponseEntity<ApiResponse< PaginatedResponse<ProductResponseDTO>>>
	    getAllProducts(

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
	                    "Page number cannot be negative");
	        }

	        if (size <= 0) {
	            throw new IllegalArgumentException(
	                    "Page size must be greater than zero");
	        }

	        Sort.Direction sortDirection =
	                direction.equalsIgnoreCase("DESC")
	                        ? Sort.Direction.DESC
	                        : Sort.Direction.ASC;

	        Pageable pageable = PageRequest.of( page,size,Sort.by(sortDirection, sortBy));

	        PaginatedResponse<ProductResponseDTO> response =
	                productService.getAllProducts(pageable);

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        response,
	                        "Products retrieved successfully"));
	    }

	    @PutMapping("/{id}")
	    public ResponseEntity<ApiResponse<ProductResponseDTO>>
	            updateProduct( @PathVariable Long id,@Valid @RequestBody ProductRequestDTO requestDTO) {

	        ProductResponseDTO response = productService.updateProduct(id,requestDTO);

	        return ResponseEntity.ok(
	                ApiResponse.success(
	                        response,
	                        "Product updated successfully"));
	    }
}
