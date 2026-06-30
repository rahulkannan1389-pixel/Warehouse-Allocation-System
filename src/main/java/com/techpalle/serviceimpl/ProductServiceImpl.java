package com.techpalle.serviceimpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techpalle.dto.request.ProductRequestDTO;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.ProductResponseDTO;
import com.techpalle.entity.Product;
import com.techpalle.exception.BusinessException;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.ProductRepository;
import com.techpalle.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional

public class ProductServiceImpl implements ProductService {

	 private final ProductRepository productRepository;

	    @Override
	    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
	        log.info("Creating product with SKU: {}", requestDTO.getSku());

	        requestDTO.normalize(); 

	        if (productRepository.existsBySku(requestDTO.getSku())) {
	            throw new BusinessException("Product already exists with SKU: " + requestDTO.getSku());
	        }

	        Product product = Product.builder()
	                .name(requestDTO.getName())
	                .sku(requestDTO.getSku())
	                .build();

	        Product saved = productRepository.save(product);

	        log.info("Product created with ID: {}", saved.getId());

	        return mapToDTO(saved);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public ProductResponseDTO getProductById(Long id) {

	        Product product = productRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

	        return mapToDTO(product);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public PaginatedResponse<ProductResponseDTO> getAllProducts(Pageable pageable) {

	        Page<Product> page = productRepository.findAll(pageable);

	        return PaginatedResponse.<ProductResponseDTO>builder()
	                .content(page.getContent().stream().map(this::mapToDTO).toList())
	                .pageNumber(page.getNumber())
	                .pageSize(page.getSize())
	                .totalElements(page.getTotalElements())
	                .totalPages(page.getTotalPages())
	                .isFirst(page.isFirst())
	                .isLast(page.isLast())
	                .hasNext(page.hasNext())
	                .hasPrevious(page.hasPrevious())
	                .build();
	    }

	    @Override
	    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {

	        requestDTO.normalize();

	        Product product = productRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

	        if (!product.getSku().equals(requestDTO.getSku()) &&
	                productRepository.existsBySku(requestDTO.getSku())) {

	            throw new BusinessException("SKU already exists: " + requestDTO.getSku());
	        }

	        product.setName(requestDTO.getName());
	        product.setSku(requestDTO.getSku());

	        Product updated = productRepository.save(product);

	        return mapToDTO(updated);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public boolean productExists(Long id) {
	        return productRepository.existsById(id);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public boolean productExistsBySku(String sku) {
	        return productRepository.existsBySku(sku);
	    }

	    // Mapper
	    private ProductResponseDTO mapToDTO(Product product) {
	        return ProductResponseDTO.builder()
	                .id(product.getId())
	                .name(product.getName())
	                .sku(product.getSku())
	                .createdAt(product.getCreatedAt())
	                .updatedAt(product.getUpdatedAt())
	                .build();
	    }
}
