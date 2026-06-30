package com.techpalle;

import com.techpalle.dto.request.AllocationRequestDTO;
import com.techpalle.dto.request.AutoAllocationRequestDTO;
import com.techpalle.dto.response.AllocationResponseDTO;
import com.techpalle.entity.*;
import com.techpalle.enums.AllocationStatus;
import com.techpalle.enums.WarehouseStatus;
import com.techpalle.exception.AllocationException;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.*;
import com.techpalle.serviceimpl.AllocationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Allocation Service Tests")

public class AllocationServiceTest {

	  @Mock
	    private AllocationRepository allocationRepository;

	    @Mock
	    private WarehouseInventoryRepository inventoryRepository;

	    @Mock
	    private WarehouseRepository warehouseRepository;

	    @Mock
	    private ProductRepository productRepository;

	    @InjectMocks
	    private AllocationServiceImpl allocationService;

	    private Warehouse warehouse;
	    private Product product;
	    private WarehouseInventory inventory;
	    private AllocationRequestDTO requestDTO;

	    @BeforeEach
	    void setUp() {

	        MockitoAnnotations.openMocks(this);

	        warehouse = Warehouse.builder()
	                .id(1L)
	                .name("Main Warehouse")
	                .location("Bangalore")
	                .capacity(10000L)
	                .usedCapacity(0L)
	                .status(WarehouseStatus.ACTIVE)
	                .deletedAt(null)
	                .build();

	        product = Product.builder()
	                .id(1L)
	                .name("Laptop")
	                .sku("LAPTOP-001")
	                .build();

	        inventory = WarehouseInventory.builder()
	                .id(1L)
	                .warehouse(warehouse)
	                .product(product)
	                .availableQuantity(100L)
	                .build();

	        requestDTO = AllocationRequestDTO.builder()
	                .requestId("REQ-001")
	                .productId(1L)
	                .warehouseId(1L)
	                .quantity(10L)
	                .notes("Test Allocation")
	                .build();
	    }

	    @Test
	    @DisplayName("Should allocate product successfully")
	    void shouldAllocateProductSuccessfully() {

	        when(allocationRepository.findByRequestId("REQ-001"))
	                .thenReturn(Optional.empty());

	        when(productRepository.findById(1L))
	                .thenReturn(Optional.of(product));

	        when(warehouseRepository.findByIdAndDeletedAtIsNull(1L))
	                .thenReturn(Optional.of(warehouse));

	        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L))
	                .thenReturn(Optional.of(inventory));

	        when(allocationRepository.save(any()))
	                .thenReturn(createAllocation());

	        AllocationResponseDTO response =
	                allocationService.allocateProductToWarehouse(
	                        requestDTO,
	                        "ADMIN");

	        assertNotNull(response);
	        assertEquals(10L, response.getQuantity());

	        verify(allocationRepository, times(1))
	                .save(any(Allocation.class));
	    }

	    @Test
	    @DisplayName("Should reject duplicate requestId")
	    void shouldRejectDuplicateRequestId() {

	        when(allocationRepository.findByRequestId("REQ-001"))
	                .thenReturn(Optional.of(createAllocation()));

	        assertThrows(
	                AllocationException.class,
	                () -> allocationService
	                        .allocateProductToWarehouse(
	                                requestDTO,
	                                "ADMIN"));
	    }

	    @Test
	    @DisplayName("Should throw when product not found")
	    void shouldThrowWhenProductNotFound() {

	        when(allocationRepository.findByRequestId("REQ-001"))
	                .thenReturn(Optional.empty());

	        when(productRepository.findById(1L))
	                .thenReturn(Optional.empty());

	        assertThrows(
	                ResourceNotFoundException.class,
	                () -> allocationService
	                        .allocateProductToWarehouse(
	                                requestDTO,
	                                "ADMIN"));
	    }

	    @Test
	    @DisplayName("Should throw when warehouse not found")
	    void shouldThrowWhenWarehouseNotFound() {

	        when(allocationRepository.findByRequestId("REQ-001"))
	                .thenReturn(Optional.empty());

	        when(productRepository.findById(1L))
	                .thenReturn(Optional.of(product));

	        when(warehouseRepository.findByIdAndDeletedAtIsNull(1L))
	                .thenReturn(Optional.empty());

	        assertThrows(
	                ResourceNotFoundException.class,
	                () -> allocationService
	                        .allocateProductToWarehouse(
	                                requestDTO,
	                                "ADMIN"));
	    }

	    @Test
	    @DisplayName("Should throw when warehouse inactive")
	    void shouldThrowWhenWarehouseInactive() {

	        warehouse.setStatus(WarehouseStatus.INACTIVE);

	        when(allocationRepository.findByRequestId("REQ-001"))
	                .thenReturn(Optional.empty());

	        when(productRepository.findById(1L))
	                .thenReturn(Optional.of(product));

	        when(warehouseRepository.findByIdAndDeletedAtIsNull(1L))
	                .thenReturn(Optional.of(warehouse));

	        assertThrows(
	                AllocationException.class,
	                () -> allocationService
	                        .allocateProductToWarehouse(
	                                requestDTO,
	                                "ADMIN"));
	    }

	    @Test
	    @DisplayName("Should throw when stock insufficient")
	    void shouldThrowWhenStockInsufficient() {

	        inventory.setAvailableQuantity(5L);

	        when(allocationRepository.findByRequestId("REQ-001"))
	                .thenReturn(Optional.empty());

	        when(productRepository.findById(1L))
	                .thenReturn(Optional.of(product));

	        when(warehouseRepository.findByIdAndDeletedAtIsNull(1L))
	                .thenReturn(Optional.of(warehouse));

	        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L))
	                .thenReturn(Optional.of(inventory));

	        assertThrows(
	                AllocationException.class,
	                () -> allocationService
	                        .allocateProductToWarehouse(
	                                requestDTO,
	                                "ADMIN"));
	    }

	    @Test
	    @DisplayName("Should auto allocate product")
	    void shouldAutoAllocateProduct() {

	        AutoAllocationRequestDTO autoRequest =
	                AutoAllocationRequestDTO.builder()
	                        .requestId("REQ-002")
	                        .productId(1L)
	                        .quantity(10L)
	                        .notes("Auto")
	                        .build();

	        when(allocationRepository.findByRequestId("REQ-002"))
	                .thenReturn(Optional.empty());

	        when(productRepository.findById(1L))
	                .thenReturn(Optional.of(product));

	        when(inventoryRepository.findAvailableStock(1L))
	                .thenReturn(List.of(inventory));

	        when(allocationRepository.save(any()))
	                .thenReturn(createAllocation());

	        AllocationResponseDTO response =
	                allocationService.autoAllocateProduct(
	                        autoRequest,
	                        "ADMIN");

	        assertNotNull(response);
	    }

	    @Test
	    @DisplayName("Should cancel allocation")
	    void shouldCancelAllocation() {

	        Allocation allocation = createAllocation();

	        when(allocationRepository.findById(1L))
	                .thenReturn(Optional.of(allocation));

	        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L))
	                .thenReturn(Optional.of(inventory));

	        when(allocationRepository.save(any()))
	                .thenReturn(allocation);

	        AllocationResponseDTO response =
	                allocationService.cancelAllocation(1L);

	        assertNotNull(response);

	        verify(allocationRepository)
	                .save(any(Allocation.class));
	    }

	    private Allocation createAllocation() {

	        return Allocation.builder()
	                .id(1L)
	                .requestId("REQ-001")
	                .product(product)
	                .warehouse(warehouse)
	                .quantity(10L)
	                .status(AllocationStatus.CONFIRMED)
	                .allocatedAt(LocalDateTime.now())
	                .allocatedBy("ADMIN")
	                .notes("Test")
	                .build();
	    }

	

}
