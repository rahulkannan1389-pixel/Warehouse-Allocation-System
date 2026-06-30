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
public class AutoAllocationRequestDTO {

    @NotBlank(message = "Request ID is required")
    private String requestId;   

    @NotNull
    @Positive
    private Long productId;

    @NotNull
    @Positive
    private Long quantity;

    @Size(max = 500)
    private String notes;

    public void normalize() {
        if (requestId != null) requestId = requestId.trim();
        if (notes != null) notes = notes.trim();
    }
}
