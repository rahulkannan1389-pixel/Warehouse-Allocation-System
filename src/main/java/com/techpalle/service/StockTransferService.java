package com.techpalle.service;

import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import com.techpalle.dto.request.StockTransferRequestDTO;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.StockTransferResponseDTO;

public interface StockTransferService {

    StockTransferResponseDTO initiateTransfer(StockTransferRequestDTO requestDTO, String transferredBy);

    StockTransferResponseDTO getTransferById(Long id);

    PaginatedResponse<StockTransferResponseDTO> getAllTransfers(Pageable pageable);

    PaginatedResponse<StockTransferResponseDTO> getTransfersFromWarehouse(Long warehouseId, Pageable pageable);

    PaginatedResponse<StockTransferResponseDTO> getTransfersToWarehouse(Long warehouseId, Pageable pageable);

    PaginatedResponse<StockTransferResponseDTO> getTransfersByProduct(Long productId, Pageable pageable);

    PaginatedResponse<StockTransferResponseDTO> getTransfersByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    //Workflow steps
    StockTransferResponseDTO completeTransfer(Long transferId);

    StockTransferResponseDTO cancelTransfer(Long transferId);

    // StockTransferResponseDTO failTransfer(Long transferId);
}
