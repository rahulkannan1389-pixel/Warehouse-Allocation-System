package com.techpalle.entity;

import java.time.LocalDateTime;

import com.techpalle.enums.AllocationStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "allocations",
       indexes = {
           @Index(name = "idx_alloc_product", columnList = "product_id"),
           @Index(name = "idx_alloc_warehouse", columnList = "warehouse_id"),
           @Index(name = "idx_alloc_date", columnList = "allocated_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allocation  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", unique = true)
    private String requestId; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllocationStatus status;

    @Column(name = "allocated_at", nullable = false)
    private LocalDateTime allocatedAt;

    @Column(name = "allocated_by", nullable = false)
    private String allocatedBy;

    @Column(length = 500)
    private String notes;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @PrePersist
    protected void prePersist() {
        super.onCreate();
        if (allocatedAt == null) {
            allocatedAt = LocalDateTime.now();
        }
    }
}
