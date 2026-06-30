package com.techpalle.dto.response;

import java.time.LocalDateTime;
import com.techpalle.enums.AllocationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationResponseDTO {

    private Long id;

    private String requestId; 

    private Long productId;

    private String productName;

    private String productSku;

    private Long warehouseId;

    private String warehouseName;

    private Long quantity;

    private AllocationStatus status; 

    private LocalDateTime allocatedAt;

    private String allocatedBy;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt; 


}
