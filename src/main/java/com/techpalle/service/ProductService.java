package com.techpalle.service;

import org.springframework.data.domain.Pageable;

import com.techpalle.dto.request.ProductRequestDTO;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.ProductResponseDTO;

public interface ProductService {

	    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);

	    ProductResponseDTO getProductById(Long id);

	    PaginatedResponse<ProductResponseDTO> getAllProducts(Pageable pageable);

	    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO);

	    boolean productExists(Long id);

	    boolean productExistsBySku(String sku);
}
