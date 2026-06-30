package com.techpalle.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class WarehouseInventoryResponseDTO {

    private Long id;

    private Long warehouseId;

    private String warehouseName;

    private Long productId;

    private String productName;

    private String productSku;

    private Long availableQuantity;

    private LocalDateTime updatedAt;

}
