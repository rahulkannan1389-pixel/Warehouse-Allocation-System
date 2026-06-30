package com.techpalle.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransferRequestDTO {

    @NotNull
    @Positive
    private Long sourceWarehouseId;

    @NotNull
    @Positive
    private Long targetWarehouseId;

    @NotNull
    @Positive
    private Long productId;

    @NotNull
    @Positive
    private Long quantity;

    @Size(max = 500)
    private String notes;

    public void normalize() {
        if (notes != null) notes = notes.trim();
    }

    public void validate() {
        if (sourceWarehouseId != null && sourceWarehouseId.equals(targetWarehouseId)) {
            throw new IllegalArgumentException("Source and target warehouses cannot be the same");
        }
    }
}
