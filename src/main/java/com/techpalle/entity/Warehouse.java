package com.techpalle.entity;

import java.time.LocalDateTime;
import com.techpalle.enums.WarehouseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "warehouses",
       indexes = {
           @Index(name = "idx_warehouse_name", columnList = "name")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false)
    private Long capacity;

    @Column(name = "used_capacity", nullable = false)
    private Long usedCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarehouseStatus status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void prePersist() {
        super.onCreate();
        if (status == null) status = WarehouseStatus.INACTIVE;
        if (usedCapacity == null) usedCapacity = 0L;
    }

}
