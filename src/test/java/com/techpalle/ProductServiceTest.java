package com.techpalle;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.techpalle.dto.request.ProductRequestDTO;
import com.techpalle.dto.response.ProductResponseDTO;
import com.techpalle.entity.Product;
import com.techpalle.exception.BusinessException;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.ProductRepository;
import com.techpalle.serviceimpl.ProductServiceImpl;

import java.util.List;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@DisplayName("Product Service Tests")
public class ProductServiceTest {


    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        requestDTO = ProductRequestDTO.builder()
                .name("Laptop")
                .sku("LAPTOP-001")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .sku("LAPTOP-001")
                .build();
    }

    @Test
    void shouldCreateProductSuccessfully() {

        when(productRepository.existsBySku("LAPTOP-001"))
                .thenReturn(false);

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponseDTO response =
                productService.createProduct(requestDTO);

        assertNotNull(response);
        assertEquals("Laptop", response.getName());

        verify(productRepository)
                .save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenSkuExists() {

        when(productRepository.existsBySku("LAPTOP-001"))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> productService.createProduct(requestDTO));
    }

    @Test
    void shouldGetProductById() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponseDTO response =
                productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(99L));
    }

    @Test
    void shouldGetAllProducts() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page =
                new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable))
                .thenReturn(page);

        var response =
                productService.getAllProducts(pageable);

        assertEquals(1,
                response.getContent().size());
    }

    @Test
    void shouldUpdateProductSuccessfully() {

        ProductRequestDTO updateRequest =
                ProductRequestDTO.builder()
                        .name("Gaming Laptop")
                        .sku("LAPTOP-002")
                        .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.existsBySku("LAPTOP-002"))
                .thenReturn(false);

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponseDTO response =
                productService.updateProduct(
                        1L,
                        updateRequest);

        assertNotNull(response);

        verify(productRepository)
                .save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDuplicateSku() {

        ProductRequestDTO updateRequest =
                ProductRequestDTO.builder()
                        .name("Gaming Laptop")
                        .sku("LAPTOP-002")
                        .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.existsBySku("LAPTOP-002"))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> productService.updateProduct(
                        1L,
                        updateRequest));
    }

    @Test
    void shouldReturnTrueWhenProductExists() {

        when(productRepository.existsById(1L))
                .thenReturn(true);

        assertTrue(
                productService.productExists(1L));
    }

    @Test
    void shouldReturnTrueWhenSkuExists() {

        when(productRepository.existsBySku("LAPTOP-001"))
                .thenReturn(true);

        assertTrue(
                productService.productExistsBySku(
                        "LAPTOP-001"));
    }

}
