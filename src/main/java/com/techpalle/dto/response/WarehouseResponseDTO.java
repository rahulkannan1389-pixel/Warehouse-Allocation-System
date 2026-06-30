package com.techpalle.dto.response;

import java.time.LocalDateTime;
import com.techpalle.enums.WarehouseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseResponseDTO {

    private Long id;

    private String name;

    private String location;

    private Long capacity;

    private Long usedCapacity; 

    private WarehouseStatus status; 

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

