package com.techpalle.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.WarehouseInventoryResponseDTO;

public interface WarehouseInventoryService {

	   WarehouseInventoryResponseDTO getInventory(Long warehouseId, Long productId);

	    PaginatedResponse<WarehouseInventoryResponseDTO> getWarehouseInventory(Long warehouseId, Pageable pageable);

	    PaginatedResponse<WarehouseInventoryResponseDTO> getProductInventory(Long productId, Pageable pageable);

	    List<WarehouseInventoryResponseDTO> getAvailableWarehouses(Long productId);

	    Long getTotalAvailableStock(Long productId);

	    //  Adjust (increment/decrement)
	    WarehouseInventoryResponseDTO adjustInventory(
	            Long warehouseId,
	            Long productId,
	            Long quantityChange
	    );

	    //  Set absolute value
	    WarehouseInventoryResponseDTO setInventory(
	            Long warehouseId,
	            Long productId,
	            Long quantity
	    );

	    WarehouseInventoryResponseDTO initializeInventory(Long warehouseId, Long productId, Long quantity);
	}
