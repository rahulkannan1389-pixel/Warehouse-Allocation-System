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
public class WarehouseRequestDTO {

    @NotBlank(message = "Warehouse name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Location is required")
    @Size(min = 5, max = 255)
    private String location;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than 0")
    private Long capacity;

    // Normalize input
    public void normalize() {
        if (name != null) name = name.trim();
        if (location != null) location = location.trim();
    }
}
