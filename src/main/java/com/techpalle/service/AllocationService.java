package com.techpalle.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;

import com.techpalle.dto.request.AllocationRequestDTO;
import com.techpalle.dto.request.AutoAllocationRequestDTO;
import com.techpalle.dto.response.AllocationResponseDTO;
import com.techpalle.dto.response.PaginatedResponse;

public interface AllocationService {


	 AllocationResponseDTO allocateProductToWarehouse(
	            AllocationRequestDTO requestDTO,
	            String allocatedBy);

	    AllocationResponseDTO autoAllocateProduct(
	            AutoAllocationRequestDTO requestDTO,
	            String allocatedBy);

	    AllocationResponseDTO getByRequestId(String requestId);

	    AllocationResponseDTO getAllocationById(Long id);

	    PaginatedResponse<AllocationResponseDTO> getAllAllocations(
	            Pageable pageable);

	    PaginatedResponse<AllocationResponseDTO> getAllocationsByProduct(
	            Long productId,
	            Pageable pageable);

	    PaginatedResponse<AllocationResponseDTO> getAllocationsByWarehouse(
	            Long warehouseId,
	            Pageable pageable);

	    PaginatedResponse<AllocationResponseDTO>
	    getAllocationsByProductAndDateRange(
	            Long productId,
	            LocalDateTime startDate,
	            LocalDateTime endDate,
	            Pageable pageable);

	    PaginatedResponse<AllocationResponseDTO>
	    getAllocationsByWarehouseAndDateRange(
	            Long warehouseId,
	            LocalDateTime startDate,
	            LocalDateTime endDate,
	            Pageable pageable);

	    PaginatedResponse<AllocationResponseDTO>
	    getAllocationsByDateRange(
	            LocalDateTime startDate,
	            LocalDateTime endDate,
	            Pageable pageable);

	    AllocationResponseDTO cancelAllocation(Long allocationId);

	    AllocationResponseDTO confirmAllocation(Long allocationId);


}
