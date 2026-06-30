package com.techpalle;


import com.techpalle.dto.request.WarehouseRequestDTO;
import com.techpalle.dto.response.WarehouseResponseDTO;
import com.techpalle.entity.Warehouse;
import com.techpalle.enums.WarehouseStatus;
import com.techpalle.exception.BusinessException;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.WarehouseRepository;
import com.techpalle.serviceimpl.WarehouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@DisplayName("Warehouse Service Tests")
public class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Warehouse warehouse;
    private WarehouseRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        requestDTO = WarehouseRequestDTO.builder()
                .name("Main Warehouse")
                .location("Bangalore")
                .capacity(10000L)
                .build();

        warehouse = Warehouse.builder()
                .id(1L)
                .name("Main Warehouse")
                .location("Bangalore")
                .capacity(10000L)
                .usedCapacity(0L)
                .status(WarehouseStatus.INACTIVE)
                .deletedAt(null)
                .build();
    }

    @Test
    void shouldCreateWarehouseSuccessfully() {

        when(warehouseRepository
                .findByNameAndDeletedAtIsNull(
                        requestDTO.getName()))
                .thenReturn(Optional.empty());

        when(warehouseRepository.save(any()))
                .thenReturn(warehouse);

        WarehouseResponseDTO response =
                warehouseService.createWarehouse(
                        requestDTO);

        assertNotNull(response);
        assertEquals(
                "Main Warehouse",
                response.getName());
    }

    @Test
    void shouldThrowExceptionWhenWarehouseExists() {

        when(warehouseRepository
                .findByNameAndDeletedAtIsNull(
                        requestDTO.getName()))
                .thenReturn(Optional.of(warehouse));

        assertThrows(
                BusinessException.class,
                () -> warehouseService
                        .createWarehouse(
                                requestDTO));
    }

    @Test
    void shouldGetWarehouseById() {

        when(warehouseRepository
                .findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(warehouse));

        WarehouseResponseDTO response =
                warehouseService
                        .getWarehouseById(1L);

        assertNotNull(response);
    }

    @Test
    void shouldThrowExceptionWhenWarehouseNotFound() {

        when(warehouseRepository
                .findByIdAndDeletedAtIsNull(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> warehouseService
                        .getWarehouseById(99L));
    }

    @Test
    void shouldGetAllWarehouses() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Warehouse> page =
                new PageImpl<>(List.of(warehouse));

        when(warehouseRepository
                .findByDeletedAtIsNull(pageable))
                .thenReturn(page);

        var response =
                warehouseService.getAllWarehouses(
                        pageable);

        assertEquals(
                1,
                response.getContent().size());
    }

    @Test
    void shouldUpdateWarehouseSuccessfully() {

        when(warehouseRepository
                .findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(warehouse));

        when(warehouseRepository
                .findByNameAndDeletedAtIsNull(
                        "Main Warehouse"))
                .thenReturn(Optional.empty());

        when(warehouseRepository.save(any()))
                .thenReturn(warehouse);

        WarehouseResponseDTO response =
                warehouseService.updateWarehouse(
                        1L,
                        requestDTO);

        assertNotNull(response);
    }

    @Test
    void shouldActivateWarehouse() {

        when(warehouseRepository
                .findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(warehouse));

        when(warehouseRepository.save(any()))
                .thenReturn(warehouse);

        WarehouseResponseDTO response =
                warehouseService.activateWarehouse(1L);

        assertNotNull(response);
    }

    @Test
    void shouldDeactivateWarehouse() {

        warehouse.setStatus(WarehouseStatus.ACTIVE);

        when(warehouseRepository
                .findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(warehouse));

        when(warehouseRepository.save(any()))
                .thenReturn(warehouse);

        WarehouseResponseDTO response =
                warehouseService.deactivateWarehouse(1L);

        assertNotNull(response);
    }

    @Test
    void shouldDeleteWarehouse() {

        when(warehouseRepository
                .findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(warehouse));

        warehouseService.deleteWarehouse(1L);

        verify(warehouseRepository)
                .save(any(Warehouse.class));
    }

    @Test
    void shouldReturnTrueWhenWarehouseExists() {

        when(warehouseRepository
                .findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(warehouse));

        assertTrue(
                warehouseService.warehouseExists(1L));
    }

    @Test
    void shouldReturnTrueWhenWarehouseNameExists() {

        when(warehouseRepository
                .findByNameAndDeletedAtIsNull(
                        "Main Warehouse"))
                .thenReturn(Optional.of(warehouse));

        assertTrue(
                warehouseService
                        .warehouseExistsByName(
                                "Main Warehouse"));
    }
}

