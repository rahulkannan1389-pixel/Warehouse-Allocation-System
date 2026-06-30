package com.techpalle.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techpalle.dto.request.AllocationRequestDTO;
import com.techpalle.dto.request.AutoAllocationRequestDTO;
import com.techpalle.dto.response.AllocationResponseDTO;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.entity.Allocation;
import com.techpalle.entity.Product;
import com.techpalle.entity.Warehouse;
import com.techpalle.entity.WarehouseInventory;
import com.techpalle.enums.AllocationStatus;
import com.techpalle.enums.WarehouseStatus;
import com.techpalle.exception.AllocationException;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.AllocationRepository;
import com.techpalle.repository.ProductRepository;
import com.techpalle.repository.WarehouseInventoryRepository;
import com.techpalle.repository.WarehouseRepository;
import com.techpalle.service.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AllocationServiceImpl implements AllocationService {


	    private final AllocationRepository allocationRepository;
	    private final WarehouseInventoryRepository inventoryRepository;
	    private final WarehouseRepository warehouseRepository;
	    private final ProductRepository productRepository;

	    @Override
	    public AllocationResponseDTO allocateProductToWarehouse(
	            AllocationRequestDTO requestDTO,
	            String allocatedBy) {

	        log.info("Allocating product {} to warehouse {}",
	                requestDTO.getProductId(),
	                requestDTO.getWarehouseId());

	        validateRequest(requestDTO.getQuantity());
	        checkIdempotency(requestDTO.getRequestId());

	        Product product = validateProduct(requestDTO.getProductId());

	        Warehouse warehouse = validateWarehouse(
	                requestDTO.getWarehouseId());

	        if (warehouse.getStatus() != WarehouseStatus.ACTIVE) {
	            throw new AllocationException("Warehouse is not active");
	        }

	        WarehouseInventory inventory =
	                inventoryRepository.findByWarehouseIdAndProductId(
	                                warehouse.getId(),
	                                product.getId())
	                        .orElseThrow(() ->
	                                new AllocationException(
	                                        "Inventory not found"));

	        if (inventory.getAvailableQuantity()
	                < requestDTO.getQuantity()) {

	            throw new AllocationException(
	                    "Insufficient stock. Available: "
	                            + inventory.getAvailableQuantity());
	        }

	        inventory.setAvailableQuantity(
	                inventory.getAvailableQuantity()
	                        - requestDTO.getQuantity());

	        inventoryRepository.save(inventory);

	        Allocation allocation = Allocation.builder()
	                .requestId(requestDTO.getRequestId())
	                .product(product)
	                .warehouse(warehouse)
	                .quantity(requestDTO.getQuantity())
	                .status(AllocationStatus.CONFIRMED)
	                .allocatedBy(allocatedBy)
	                .notes(requestDTO.getNotes())
	                .allocatedAt(LocalDateTime.now())
	                .build();

	        Allocation saved = allocationRepository.save(allocation);

	        log.info("Allocation created successfully with ID {}",
	                saved.getId());

	        return mapToDTO(saved);
	    }

	    @Override
	    public AllocationResponseDTO autoAllocateProduct(
	            AutoAllocationRequestDTO requestDTO,
	            String allocatedBy) {

	        log.info("Auto allocating product {} quantity {}",
	                requestDTO.getProductId(),
	                requestDTO.getQuantity());

	        validateRequest(requestDTO.getQuantity());
	        checkIdempotency(requestDTO.getRequestId());

	        Product product = validateProduct(
	                requestDTO.getProductId());

	        List<WarehouseInventory> inventories =
	                inventoryRepository.findAvailableStock(
	                        product.getId());

	        WarehouseInventory selectedInventory =
	                inventories.stream()
	                        .filter(i ->
	                                i.getWarehouse().getStatus()
	                                        == WarehouseStatus.ACTIVE)
	                        .filter(i ->
	                                i.getAvailableQuantity()
	                                        >= requestDTO.getQuantity())
	                        .findFirst()
	                        .orElseThrow(() ->
	                                new AllocationException(
	                                        "No suitable warehouse found"));

	        selectedInventory.setAvailableQuantity(
	                selectedInventory.getAvailableQuantity()
	                        - requestDTO.getQuantity());

	        inventoryRepository.save(selectedInventory);

	        Allocation allocation = Allocation.builder()
	                .requestId(requestDTO.getRequestId())
	                .product(product)
	                .warehouse(selectedInventory.getWarehouse())
	                .quantity(requestDTO.getQuantity())
	                .status(AllocationStatus.CONFIRMED)
	                .allocatedBy(allocatedBy)
	                .notes(requestDTO.getNotes())
	                .allocatedAt(LocalDateTime.now())
	                .build();

	        Allocation saved = allocationRepository.save(allocation);

	        return mapToDTO(saved);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public AllocationResponseDTO getByRequestId(String requestId) {

	        Allocation allocation =
	                allocationRepository.findByRequestId(requestId)
	                        .orElseThrow(() ->
	                                new ResourceNotFoundException(
	                                        "Allocation",
	                                        "requestId",
	                                        requestId));

	        return mapToDTO(allocation);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public AllocationResponseDTO getAllocationById(Long id) {

	        Allocation allocation =
	                allocationRepository.findById(id)
	                        .orElseThrow(() ->
	                                new ResourceNotFoundException(
	                                        "Allocation",
	                                        "id",
	                                        id));

	        return mapToDTO(allocation);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public PaginatedResponse<AllocationResponseDTO> getAllAllocations(
	            Pageable pageable) {

	        return buildPageResponse(
	                allocationRepository.findAll(pageable));
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public PaginatedResponse<AllocationResponseDTO>
	    getAllocationsByProduct(
	            Long productId,
	            Pageable pageable) {

	        validateProduct(productId);

	        return buildPageResponse(
	                allocationRepository.findByProductId(
	                        productId,
	                        pageable));
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public PaginatedResponse<AllocationResponseDTO>
	    getAllocationsByWarehouse(
	            Long warehouseId,
	            Pageable pageable) {

	        validateWarehouse(warehouseId);

	        return buildPageResponse(
	                allocationRepository.findByWarehouseId(
	                        warehouseId,
	                        pageable));
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public PaginatedResponse<AllocationResponseDTO>
	    getAllocationsByProductAndDateRange(
	            Long productId,
	            LocalDateTime startDate,
	            LocalDateTime endDate,
	            Pageable pageable) {

	        validateProduct(productId);

	        return buildPageResponse(
	                allocationRepository.findByProductAndDateRange(
	                        productId,
	                        startDate,
	                        endDate,
	                        pageable));
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public PaginatedResponse<AllocationResponseDTO>
	    getAllocationsByWarehouseAndDateRange(
	            Long warehouseId,
	            LocalDateTime startDate,
	            LocalDateTime endDate,
	            Pageable pageable) {

	        validateWarehouse(warehouseId);

	        return buildPageResponse(
	                allocationRepository.findByWarehouseAndDateRange(
	                        warehouseId,
	                        startDate,
	                        endDate,
	                        pageable));
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public PaginatedResponse<AllocationResponseDTO>
	    getAllocationsByDateRange(
	            LocalDateTime startDate,
	            LocalDateTime endDate,
	            Pageable pageable) {

	        return buildPageResponse(
	                allocationRepository.findByDateRange(
	                        startDate,
	                        endDate,
	                        pageable));
	    }

	    @Override
	    public AllocationResponseDTO cancelAllocation(Long allocationId) {

	        Allocation allocation =
	                allocationRepository.findById(allocationId)
	                        .orElseThrow(() ->
	                                new ResourceNotFoundException(
	                                        "Allocation",
	                                        "id",
	                                        allocationId));

	        if (allocation.getStatus() != AllocationStatus.CONFIRMED) {
	            throw new AllocationException(
	                    "Only confirmed allocations can be cancelled");
	        }

	        WarehouseInventory inventory =
	                inventoryRepository.findByWarehouseIdAndProductId(
	                                allocation.getWarehouse().getId(),
	                                allocation.getProduct().getId())
	                        .orElseThrow(() ->
	                                new AllocationException(
	                                        "Inventory not found"));

	        inventory.setAvailableQuantity(
	                inventory.getAvailableQuantity()
	                        + allocation.getQuantity());

	        inventoryRepository.save(inventory);

	        allocation.setStatus(AllocationStatus.CANCELLED);
	        allocation.setCancelledAt(LocalDateTime.now());

	        return mapToDTO(
	                allocationRepository.save(allocation));
	    }

	    @Override
	    public AllocationResponseDTO confirmAllocation(Long allocationId) {

	        Allocation allocation =
	                allocationRepository.findById(allocationId)
	                        .orElseThrow(() ->
	                                new ResourceNotFoundException(
	                                        "Allocation",
	                                        "id",
	                                        allocationId));

	        if (allocation.getStatus()
	                == AllocationStatus.CONFIRMED) {

	            throw new AllocationException(
	                    "Allocation already confirmed");
	        }

	        allocation.setStatus(
	                AllocationStatus.CONFIRMED);

	        return mapToDTO(
	                allocationRepository.save(allocation));
	    }

	    private void validateRequest(Long quantity) {

	        if (quantity == null || quantity <= 0) {
	            throw new AllocationException(
	                    "Quantity must be greater than zero");
	        }
	    }

	    private void checkIdempotency(String requestId) {

	        if (requestId != null &&
	                allocationRepository.findByRequestId(requestId)
	                        .isPresent()) {

	            throw new AllocationException(
	                    "Duplicate request detected");
	        }
	    }

	    private Product validateProduct(Long productId) {

	        return productRepository.findById(productId)
	                .orElseThrow(() ->
	                        new ResourceNotFoundException(
	                                "Product",
	                                "id",
	                                productId));
	    }

	    private Warehouse validateWarehouse(Long warehouseId) {

	        return warehouseRepository
	                .findByIdAndDeletedAtIsNull(warehouseId)
	                .orElseThrow(() ->
	                        new ResourceNotFoundException(
	                                "Warehouse",
	                                "id",
	                                warehouseId));
	    }

	    private PaginatedResponse<AllocationResponseDTO>
	    buildPageResponse(Page<Allocation> page) {

	        return PaginatedResponse.<AllocationResponseDTO>builder()
	                .content(page.getContent()
	                        .stream()
	                        .map(this::mapToDTO)
	                        .toList())
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

	    private AllocationResponseDTO mapToDTO(Allocation allocation) {

	        return AllocationResponseDTO.builder()
	                .id(allocation.getId())
	                .requestId(allocation.getRequestId())
	                .productId(allocation.getProduct().getId())
	                .productName(allocation.getProduct().getName())
	                .productSku(allocation.getProduct().getSku())
	                .warehouseId(allocation.getWarehouse().getId())
	                .warehouseName(allocation.getWarehouse().getName())
	                .quantity(allocation.getQuantity())
	                .status(allocation.getStatus())
	                .allocatedAt(allocation.getAllocatedAt())
	                .allocatedBy(allocation.getAllocatedBy())
	                .notes(allocation.getNotes())
	                .createdAt(allocation.getCreatedAt())
	                .updatedAt(allocation.getUpdatedAt())
	                .build();
	    }

}
