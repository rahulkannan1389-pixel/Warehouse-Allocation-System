package com.techpalle.serviceimpl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.WarehouseInventoryResponseDTO;
import com.techpalle.entity.Product;
import com.techpalle.entity.Warehouse;
import com.techpalle.entity.WarehouseInventory;
import com.techpalle.exception.BusinessException;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.ProductRepository;
import com.techpalle.repository.WarehouseInventoryRepository;
import com.techpalle.repository.WarehouseRepository;
import com.techpalle.service.WarehouseInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class WarehouseInventoryServiceImpl implements  WarehouseInventoryService {

    private final WarehouseInventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

   
    @Override
    @Transactional(readOnly = true)
    public WarehouseInventoryResponseDTO getInventory(Long warehouseId, Long productId) {

        WarehouseInventory inventory = inventoryRepository
                .findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory", "warehouseId-productId", warehouseId + "-" + productId));

        return mapToDTO(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<WarehouseInventoryResponseDTO> getWarehouseInventory(Long warehouseId, Pageable pageable) {

        validateWarehouse(warehouseId);

        Page<WarehouseInventory> page = inventoryRepository.findByWarehouseId(warehouseId, pageable);

        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<WarehouseInventoryResponseDTO> getProductInventory(Long productId, Pageable pageable) {

        validateProduct(productId);

        Page<WarehouseInventory> page = inventoryRepository.findByProductId(productId, pageable);

        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseInventoryResponseDTO> getAvailableWarehouses(Long productId) {

        validateProduct(productId);

        return inventoryRepository.findAvailableStock(productId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalAvailableStock(Long productId) {

        validateProduct(productId);

        return inventoryRepository.getTotalAvailableStock(productId);
    }

    @Override
    public WarehouseInventoryResponseDTO setInventory(Long warehouseId, Long productId, Long quantity) {

        if (quantity < 0) {
            throw new BusinessException("Quantity cannot be negative");
        }

        Warehouse warehouse = validateWarehouse(warehouseId);
        validateProduct(productId);

        WarehouseInventory inventory = inventoryRepository
                .lockInventory(warehouseId, productId) 
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "warehouse-product", warehouseId + "-" + productId));

        long newUsedCapacity = warehouse.getUsedCapacity()
                - inventory.getAvailableQuantity()
                + quantity;

        if (newUsedCapacity > warehouse.getCapacity()) {
            throw new BusinessException("Warehouse capacity exceeded");
        }

        inventory.setAvailableQuantity(quantity);
        warehouse.setUsedCapacity(newUsedCapacity);

        return mapToDTO(inventoryRepository.save(inventory));
    }

    @Override
    public WarehouseInventoryResponseDTO adjustInventory(Long warehouseId, Long productId, Long change) {

        Warehouse warehouse = validateWarehouse(warehouseId);
        validateProduct(productId);

        WarehouseInventory inventory = inventoryRepository
                .lockInventory(warehouseId, productId) 
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "warehouse-product", warehouseId + "-" + productId));

        long newQuantity = inventory.getAvailableQuantity() + change;

        if (newQuantity < 0) {
            throw new BusinessException("Insufficient stock");
        }

        long newUsedCapacity = warehouse.getUsedCapacity() + change;

        if (newUsedCapacity > warehouse.getCapacity()) {
            throw new BusinessException("Warehouse capacity exceeded");
        }

        inventory.setAvailableQuantity(newQuantity);
        warehouse.setUsedCapacity(newUsedCapacity);

        return mapToDTO(inventoryRepository.save(inventory));
    }

    @Override
    public WarehouseInventoryResponseDTO initializeInventory(Long warehouseId, Long productId, Long quantity) {

        if (quantity < 0) {
            throw new BusinessException("Quantity cannot be negative");
        }

        Warehouse warehouse = validateWarehouse(warehouseId);
        Product product = validateProduct(productId);

        if (inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId).isPresent()) {
            throw new BusinessException("Inventory already exists");
        }

        if (warehouse.getUsedCapacity() + quantity > warehouse.getCapacity()) {
            throw new BusinessException("Warehouse capacity exceeded");
        }

        WarehouseInventory inventory = WarehouseInventory.builder()
                .warehouse(warehouse)
                .product(product)
                .availableQuantity(quantity)
                .build();

        warehouse.setUsedCapacity(warehouse.getUsedCapacity() + quantity);

        return mapToDTO(inventoryRepository.save(inventory));
    }

   
    private Warehouse validateWarehouse(Long id) {
        return warehouseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
    }

    private Product validateProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    private PaginatedResponse<WarehouseInventoryResponseDTO> buildPageResponse(Page<WarehouseInventory> page) {
        return PaginatedResponse.<WarehouseInventoryResponseDTO>builder()
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

    private WarehouseInventoryResponseDTO mapToDTO(WarehouseInventory inventory) {
        return WarehouseInventoryResponseDTO.builder()
                .id(inventory.getId())
                .warehouseId(inventory.getWarehouse().getId())
                .warehouseName(inventory.getWarehouse().getName())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .productSku(inventory.getProduct().getSku())
                .availableQuantity(inventory.getAvailableQuantity())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
