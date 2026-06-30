package com.techpalle.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

	    @NotBlank(message = "Product name is required")
	    @Size(min = 2, max = 100)
	    private String name;

	    @NotBlank(message = "SKU is required")
	    @Size(min = 2, max = 50)
	    private String sku;

	    public void normalize() {
	        if (name != null) name = name.trim();
	        if (sku != null) sku = sku.trim().toUpperCase();
	    }
}
