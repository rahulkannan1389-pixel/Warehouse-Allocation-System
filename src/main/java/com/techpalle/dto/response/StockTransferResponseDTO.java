package com.techpalle.dto.response;

import java.time.LocalDateTime;
import com.techpalle.enums.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransferResponseDTO {

    private Long id;

    private Long sourceWarehouseId;

    private String sourceWarehouseName;

    private Long targetWarehouseId;

    private String targetWarehouseName;

    private Long productId;

    private String productName;

    private String productSku;

    private Long quantity;

    private TransferStatus status; 

    private LocalDateTime transferDate;

    private String transferredBy;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt; 

}
