package com.techpalle.serviceimpl;

import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techpalle.dto.request.StockTransferRequestDTO;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.StockTransferResponseDTO;
import com.techpalle.entity.Product;
import com.techpalle.entity.StockTransfer;
import com.techpalle.entity.Warehouse;
import com.techpalle.entity.WarehouseInventory;
import com.techpalle.enums.TransferStatus;
import com.techpalle.enums.WarehouseStatus;
import com.techpalle.exception.BusinessException;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.ProductRepository;
import com.techpalle.repository.StockTransferRepository;
import com.techpalle.repository.WarehouseInventoryRepository;
import com.techpalle.repository.WarehouseRepository;
import com.techpalle.service.StockTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class StockTransferServiceImpl implements StockTransferService {

    private final StockTransferRepository transferRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final WarehouseInventoryRepository inventoryRepository;

    @Override
    public StockTransferResponseDTO initiateTransfer(
            StockTransferRequestDTO requestDTO,
            String transferredBy) {

        log.info(
                "Transfer initiated from warehouse {} to warehouse {}",
                requestDTO.getSourceWarehouseId(),
                requestDTO.getTargetWarehouseId());

        validateQuantity(requestDTO.getQuantity());

        Warehouse sourceWarehouse =
                warehouseRepository.findByIdAndDeletedAtIsNull(
                                requestDTO.getSourceWarehouseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Warehouse",
                                        "id",
                                        requestDTO.getSourceWarehouseId()));

        Warehouse targetWarehouse =
                warehouseRepository.findByIdAndDeletedAtIsNull(
                                requestDTO.getTargetWarehouseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Warehouse",
                                        "id",
                                        requestDTO.getTargetWarehouseId()));

        if (sourceWarehouse.getId().equals(targetWarehouse.getId())) {
            throw new BusinessException(
                    "Source and target warehouses cannot be same");
        }

        if (sourceWarehouse.getStatus() != WarehouseStatus.ACTIVE
                || targetWarehouse.getStatus() != WarehouseStatus.ACTIVE) {

            throw new BusinessException(
                    "Both warehouses must be active");
        }

        Product product =
                productRepository.findById(requestDTO.getProductId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product",
                                        "id",
                                        requestDTO.getProductId()));

        WarehouseInventory sourceInventory =
                inventoryRepository.findByWarehouseIdAndProductId(
                                sourceWarehouse.getId(),
                                product.getId())
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Source inventory not found"));

        if (sourceInventory.getAvailableQuantity()
                < requestDTO.getQuantity()) {

            throw new BusinessException(
                    "Insufficient stock in source warehouse");
        }

        long newTargetCapacity =
                targetWarehouse.getUsedCapacity()
                        + requestDTO.getQuantity();

        if (newTargetCapacity > targetWarehouse.getCapacity()) {
            throw new BusinessException(
                    "Target warehouse capacity exceeded");
        }

        sourceInventory.setAvailableQuantity(
                sourceInventory.getAvailableQuantity()
                        - requestDTO.getQuantity());

        inventoryRepository.save(sourceInventory);

        WarehouseInventory targetInventory =
                inventoryRepository.findByWarehouseIdAndProductId(
                                targetWarehouse.getId(),
                                product.getId())
                        .orElseGet(() ->
                                WarehouseInventory.builder()
                                        .warehouse(targetWarehouse)
                                        .product(product)
                                        .availableQuantity(0L)
                                        .build());

        targetInventory.setAvailableQuantity(
                targetInventory.getAvailableQuantity()
                        + requestDTO.getQuantity());

        inventoryRepository.save(targetInventory);

        sourceWarehouse.setUsedCapacity(
                sourceWarehouse.getUsedCapacity()
                        - requestDTO.getQuantity());

        targetWarehouse.setUsedCapacity(
                targetWarehouse.getUsedCapacity()
                        + requestDTO.getQuantity());

        StockTransfer transfer = StockTransfer.builder()
                .sourceWarehouse(sourceWarehouse)
                .targetWarehouse(targetWarehouse)
                .product(product)
                .quantity(requestDTO.getQuantity())
                .status(TransferStatus.COMPLETED)
                .transferDate(LocalDateTime.now())
                .transferredBy(transferredBy)
                .notes(requestDTO.getNotes())
                .build();

        return mapToDTO(
                transferRepository.save(transfer));
    }

    @Override
    @Transactional(readOnly = true)
    public StockTransferResponseDTO getTransferById(Long id) {

        StockTransfer transfer =
                transferRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "StockTransfer",
                                        "id",
                                        id));

        return mapToDTO(transfer);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<StockTransferResponseDTO>
    getAllTransfers(Pageable pageable) {

        return buildPageResponse(
                transferRepository.findAll(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<StockTransferResponseDTO>
    getTransfersFromWarehouse(
            Long warehouseId,
            Pageable pageable) {

        validateWarehouse(warehouseId);

        return buildPageResponse(
                transferRepository.findBySourceWarehouseId(
                        warehouseId,
                        pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<StockTransferResponseDTO>
    getTransfersToWarehouse(
            Long warehouseId,
            Pageable pageable) {

        validateWarehouse(warehouseId);

        return buildPageResponse(
                transferRepository.findByTargetWarehouseId(
                        warehouseId,
                        pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<StockTransferResponseDTO>
    getTransfersByProduct(
            Long productId,
            Pageable pageable) {

        validateProduct(productId);

        return buildPageResponse(
                transferRepository.findByProductId(
                        productId,
                        pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<StockTransferResponseDTO>
    getTransfersByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }

        return buildPageResponse(
                transferRepository.findByDateRange(
                        startDate,
                        endDate,
                        pageable));
    }

    @Override
    public StockTransferResponseDTO completeTransfer(Long transferId) {

        StockTransfer transfer =
                transferRepository.findById(transferId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "StockTransfer",
                                        "id",
                                        transferId));

        if (transfer.getStatus() == TransferStatus.COMPLETED) {
            throw new BusinessException(
                    "Transfer already completed");
        }

        transfer.setStatus(TransferStatus.COMPLETED);

        return mapToDTO(
                transferRepository.save(transfer));
    }

    @Override
    public StockTransferResponseDTO cancelTransfer(Long transferId) {

        StockTransfer transfer =
                transferRepository.findById(transferId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "StockTransfer",
                                        "id",
                                        transferId));

        if (transfer.getStatus() == TransferStatus.CANCELLED) {
            throw new BusinessException(
                    "Transfer already cancelled");
        }

        transfer.setStatus(TransferStatus.CANCELLED);

        return mapToDTO(
                transferRepository.save(transfer));
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


    private void validateProduct(Long productId) {

        if (!productRepository.existsById(productId)) {

            throw new ResourceNotFoundException(
                    "Product",
                    "id",
                    productId);
        }
    }

    private void validateQuantity(Long quantity) {

        if (quantity == null || quantity <= 0) {

            throw new BusinessException(
                    "Quantity must be greater than zero");
        }
    }

    private PaginatedResponse<StockTransferResponseDTO>
    buildPageResponse(Page<StockTransfer> page) {

        return PaginatedResponse.<StockTransferResponseDTO>builder()
                .content(
                        page.getContent()
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

    private StockTransferResponseDTO mapToDTO(
            StockTransfer transfer) {

        return StockTransferResponseDTO.builder()
                .id(transfer.getId())
                .sourceWarehouseId(
                        transfer.getSourceWarehouse().getId())
                .sourceWarehouseName(
                        transfer.getSourceWarehouse().getName())
                .targetWarehouseId(
                        transfer.getTargetWarehouse().getId())
                .targetWarehouseName(
                        transfer.getTargetWarehouse().getName())
                .productId(
                        transfer.getProduct().getId())
                .productName(
                        transfer.getProduct().getName())
                .productSku(
                        transfer.getProduct().getSku())
                .quantity(transfer.getQuantity())
                .status(transfer.getStatus())
                .transferDate(transfer.getTransferDate())
                .transferredBy(transfer.getTransferredBy())
                .notes(transfer.getNotes())
                .createdAt(transfer.getCreatedAt())
                .updatedAt(transfer.getUpdatedAt())
                .build();
    }
}
