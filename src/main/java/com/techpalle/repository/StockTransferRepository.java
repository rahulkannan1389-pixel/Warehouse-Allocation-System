package com.techpalle.repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.techpalle.entity.StockTransfer;
import com.techpalle.enums.TransferStatus;

public interface StockTransferRepository extends JpaRepository<StockTransfer,Serializable> {

    Page<StockTransfer> findBySourceWarehouseId(Long warehouseId, Pageable pageable);

    Page<StockTransfer> findByTargetWarehouseId(Long warehouseId, Pageable pageable);

    Page<StockTransfer> findByProductId(Long productId, Pageable pageable);

  //  Page<StockTransfer> findAll(Pageable pageable);

    @Query("""
        SELECT st FROM StockTransfer st
        WHERE st.transferDate BETWEEN :startDate AND :endDate
    """)
    Page<StockTransfer> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("""
        SELECT st FROM StockTransfer st
        WHERE st.sourceWarehouse.id = :warehouseId
        AND st.status = :status
    """)
    List<StockTransfer> findCompletedTransfersFromWarehouse(
            @Param("warehouseId") Long warehouseId,
            @Param("status") TransferStatus status);

    @Query("""
        SELECT st FROM StockTransfer st
        WHERE st.targetWarehouse.id = :warehouseId
        AND st.status = :status
    """)
    List<StockTransfer> findCompletedTransfersToWarehouse(
            @Param("warehouseId") Long warehouseId,
            @Param("status") TransferStatus status);

    @Query("""
        SELECT COALESCE(SUM(st.quantity), 0)
        FROM StockTransfer st
        WHERE st.sourceWarehouse.id = :warehouseId
        AND st.status = :status
    """)
    Long getTotalTransferredFromWarehouse(
            @Param("warehouseId") Long warehouseId,
            @Param("status") TransferStatus status);
    


Optional<StockTransfer> findById(Long id);

// Optional<StockTransfer> findByIdAndDeletedAtIsNull(Long id);


}