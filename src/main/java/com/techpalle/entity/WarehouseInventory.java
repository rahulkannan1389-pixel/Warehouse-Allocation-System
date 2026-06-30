package com.techpalle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "warehouse_inventory",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"warehouse_id", "product_id"})
       },
       indexes = {
           @Index(name = "idx_wh_product", columnList = "warehouse_id, product_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseInventory extends BaseEntity{

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "warehouse_id", nullable = false)
	    private Warehouse warehouse;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "product_id", nullable = false)
	    private Product product;

	    @Column(name = "available_quantity", nullable = false)
	    private Long availableQuantity;

	    @PrePersist
	    @PreUpdate
	    private void validate() {
	        if (availableQuantity == null || availableQuantity < 0) {
	            throw new IllegalArgumentException("Stock cannot be negative");
	        }
	    }
}
