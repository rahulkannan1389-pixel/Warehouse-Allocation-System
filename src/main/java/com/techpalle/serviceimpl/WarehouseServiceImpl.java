package com.techpalle.serviceimpl;


import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techpalle.dto.request.WarehouseRequestDTO;
import com.techpalle.dto.response.PaginatedResponse;
import com.techpalle.dto.response.WarehouseResponseDTO;
import com.techpalle.entity.Warehouse;
import com.techpalle.enums.WarehouseStatus;
import com.techpalle.exception.BusinessException;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.WarehouseRepository;
import com.techpalle.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService  {

    private final WarehouseRepository warehouseRepository;

    @Override
    public WarehouseResponseDTO createWarehouse(WarehouseRequestDTO requestDTO) {
        log.info("Creating warehouse: {}", requestDTO.getName());

        requestDTO.normalize(); 

        if (warehouseRepository.findByNameAndDeletedAtIsNull(requestDTO.getName()).isPresent()) {
            throw new BusinessException("Warehouse already exists with name: " + requestDTO.getName());
        }

        if (requestDTO.getCapacity() <= 0) {
            throw new BusinessException("Capacity must be greater than 0");
        }

        Warehouse warehouse = Warehouse.builder()
                .name(requestDTO.getName())
                .location(requestDTO.getLocation())
                .capacity(requestDTO.getCapacity())
                .usedCapacity(0L)
                .status(WarehouseStatus.INACTIVE)
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);

        log.info("Warehouse created with ID: {}", saved.getId());

        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponseDTO getWarehouseById(Long id) {

        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

        return mapToDTO(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<WarehouseResponseDTO> getAllWarehouses(Pageable pageable) {

        Page<Warehouse> page = warehouseRepository.findByDeletedAtIsNull(pageable);

        return PaginatedResponse.<WarehouseResponseDTO>builder()
                .content(page.getContent().stream().map(this::mapToDTO).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    @Override
    public WarehouseResponseDTO updateWarehouse(Long id, WarehouseRequestDTO requestDTO) {

        requestDTO.normalize();

        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

        //  check name change
        if (!warehouse.getName().equals(requestDTO.getName()) &&
                warehouseRepository.findByNameAndDeletedAtIsNull(requestDTO.getName()).isPresent()) {

            throw new BusinessException("Warehouse name already exists");
        }

        // capacity check
        if (requestDTO.getCapacity() < warehouse.getUsedCapacity()) {
            throw new BusinessException("Capacity cannot be less than used capacity");
        }

        warehouse.setName(requestDTO.getName());
        warehouse.setLocation(requestDTO.getLocation());
        warehouse.setCapacity(requestDTO.getCapacity());

        return mapToDTO(warehouseRepository.save(warehouse));
    }

    @Override
    public WarehouseResponseDTO activateWarehouse(Long id) {

        Warehouse warehouse = getActiveWarehouse(id);

        if (warehouse.getStatus() == WarehouseStatus.ACTIVE) {
            throw new BusinessException("Warehouse already active");
        }

        warehouse.setStatus(WarehouseStatus.ACTIVE);

        return mapToDTO(warehouseRepository.save(warehouse));
    }

    @Override
    public WarehouseResponseDTO deactivateWarehouse(Long id) {

        Warehouse warehouse = getActiveWarehouse(id);

        if (warehouse.getStatus() == WarehouseStatus.INACTIVE) {
            throw new BusinessException("Warehouse already inactive");
        }

        warehouse.setStatus(WarehouseStatus.INACTIVE);

        return mapToDTO(warehouseRepository.save(warehouse));
    }

    @Override
    public void deleteWarehouse(Long id) {

        Warehouse warehouse = getActiveWarehouse(id);

        warehouse.setDeletedAt(LocalDateTime.now());

        warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean warehouseExists(Long id) {
        return warehouseRepository.findByIdAndDeletedAtIsNull(id).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean warehouseExistsByName(String name) {
        return warehouseRepository.findByNameAndDeletedAtIsNull(name).isPresent();
    }

    // Utility method
    private Warehouse getActiveWarehouse(Long id) {
        return warehouseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
    }

    // Mapper
    private WarehouseResponseDTO mapToDTO(Warehouse w) {
        return WarehouseResponseDTO.builder()
                .id(w.getId())
                .name(w.getName())
                .location(w.getLocation())
                .capacity(w.getCapacity())
                .usedCapacity(w.getUsedCapacity()) 
                .status(w.getStatus())
                .createdAt(w.getCreatedAt())
                .updatedAt(w.getUpdatedAt())
                .build();
    }	
}
