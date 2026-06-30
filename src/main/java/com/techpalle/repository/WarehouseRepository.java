package com.techpalle.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.techpalle.entity.Warehouse;
import com.techpalle.enums.WarehouseStatus;

public interface WarehouseRepository extends  JpaRepository<Warehouse, Serializable> {

	    Optional<Warehouse> findByNameAndDeletedAtIsNull(String name);

	    Optional<Warehouse> findByIdAndDeletedAtIsNull(Long id);

	    Page<Warehouse> findByDeletedAtIsNull(Pageable pageable);

	    List<Warehouse> findByStatusAndDeletedAtIsNull(WarehouseStatus status);

	    @Query("""
	        SELECT w FROM Warehouse w
	        WHERE w.deletedAt IS NULL
	        AND w.capacity >= :requiredCapacity
	        ORDER BY w.capacity ASC
	    """)
	    List<Warehouse> findWarehousesWithCapacity(@Param("requiredCapacity") Long requiredCapacity);

	    @Query("""
	        SELECT COUNT(w) FROM Warehouse w
	        WHERE w.deletedAt IS NULL AND w.status = :status
	    """)
	    long countByStatus(@Param("status") WarehouseStatus status);

}
