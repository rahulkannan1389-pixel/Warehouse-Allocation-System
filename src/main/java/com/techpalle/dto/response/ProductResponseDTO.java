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
public class ProductResponseDTO {

   private Long id;

    private String name;

    private String sku;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
