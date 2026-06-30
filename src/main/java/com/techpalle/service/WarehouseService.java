package com.techpalle.service;

import org.springframework.data.domain.Pageable;

import com.techpalle.dto.request.WarehouseRequestDTO;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.WarehouseResponseDTO;

public interface WarehouseService {

    WarehouseResponseDTO createWarehouse(WarehouseRequestDTO requestDTO);

    WarehouseResponseDTO getWarehouseById(Long id);

    PaginatedResponse<WarehouseResponseDTO> getAllWarehouses(Pageable pageable);

    WarehouseResponseDTO updateWarehouse(Long id, WarehouseRequestDTO requestDTO);

    WarehouseResponseDTO activateWarehouse(Long id);

    WarehouseResponseDTO deactivateWarehouse(Long id);

    void deleteWarehouse(Long id);

    boolean warehouseExists(Long id);

    boolean warehouseExistsByName(String name); 
}

