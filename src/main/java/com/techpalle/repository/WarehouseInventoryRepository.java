package com.techpalle.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.techpalle.entity.Product;
import com.techpalle.entity.Warehouse;
import com.techpalle.entity.WarehouseInventory;
import jakarta.persistence.LockModeType;

public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory, Serializable>{


    Optional<WarehouseInventory> findByWarehouseAndProduct(Warehouse warehouse, Product product);

    Optional<WarehouseInventory> findByWarehouseIdAndProductId(Long warehouseId, Long productId);

    List<WarehouseInventory> findByProductId(Long productId);

    List<WarehouseInventory> findByWarehouseId(Long warehouseId);

    Page<WarehouseInventory> findByProductId(Long productId, Pageable pageable);

    Page<WarehouseInventory> findByWarehouseId(Long warehouseId, Pageable pageable);

    @Query("""
        SELECT wi FROM WarehouseInventory wi
        WHERE wi.product.id = :productId
        AND wi.availableQuantity > 0
        ORDER BY wi.availableQuantity DESC
    """)
    List<WarehouseInventory> findAvailableStock(@Param("productId") Long productId);

    @Query("""
        SELECT COALESCE(SUM(wi.availableQuantity), 0)
        FROM WarehouseInventory wi
        WHERE wi.product.id = :productId
    """)
    Long getTotalAvailableStock(@Param("productId") Long productId);

    //  PESSIMISTIC LOCK
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT wi FROM WarehouseInventory wi
        WHERE wi.warehouse.id = :warehouseId
        AND wi.product.id = :productId
    """)
    Optional<WarehouseInventory> lockInventory(
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId);
}

