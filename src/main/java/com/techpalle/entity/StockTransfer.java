package com.techpalle.entity;

import java.time.LocalDateTime;
import com.techpalle.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "stock_transfers",
       indexes = {
           @Index(name = "idx_transfer_product", columnList = "product_id"),
           @Index(name = "idx_transfer_date", columnList = "transfer_date")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransfer extends BaseEntity {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "source_warehouse_id", nullable = false)
	    private Warehouse sourceWarehouse;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "target_warehouse_id", nullable = false)
	    private Warehouse targetWarehouse;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(nullable = false)
	    private Product product;

	    @Column(nullable = false)
	    private Long quantity;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private TransferStatus status;

	    @Column(name = "transfer_date", nullable = false)
	    private LocalDateTime transferDate;

	    @Column(name = "transferred_by", nullable = false)
	    private String transferredBy;

	    @Column(length = 500)
	    private String notes;

	    @PrePersist
	    protected void prePersist() {
	        super.onCreate();
	        if (transferDate == null) {
	            transferDate = LocalDateTime.now();
	        }
	        validate();
	    }
	    private void validate() {
	        if (sourceWarehouse.equals(targetWarehouse)) {
	            throw new IllegalArgumentException("Source and target warehouse cannot be same");
	        }
	    }
}
