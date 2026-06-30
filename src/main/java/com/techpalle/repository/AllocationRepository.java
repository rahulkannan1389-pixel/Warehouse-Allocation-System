package com.techpalle.repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.techpalle.entity.Allocation;

public interface AllocationRepository extends JpaRepository<Allocation, Serializable>{

    Optional<Allocation> findByRequestId(String requestId);

    Page<Allocation> findByProductId(Long productId, Pageable pageable);

    Page<Allocation> findByWarehouseId(Long warehouseId, Pageable pageable);

    @Query("""
        SELECT a
        FROM Allocation a
        WHERE a.product.id = :productId
        AND a.allocatedAt BETWEEN :startDate AND :endDate
    """)
    Page<Allocation> findByProductAndDateRange(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("""
        SELECT a
        FROM Allocation a
        WHERE a.warehouse.id = :warehouseId
        AND a.allocatedAt BETWEEN :startDate AND :endDate
    """)
    Page<Allocation> findByWarehouseAndDateRange(
            @Param("warehouseId") Long warehouseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("""
        SELECT a
        FROM Allocation a
        WHERE a.allocatedAt BETWEEN :startDate AND :endDate
    """)
    Page<Allocation> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

}
