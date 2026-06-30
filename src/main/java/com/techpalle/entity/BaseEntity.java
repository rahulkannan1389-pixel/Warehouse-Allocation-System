package com.techpalle.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract  class BaseEntity {

	    @Column(name = "created_at", nullable = false, updatable = false)
	    protected LocalDateTime createdAt;

	    @Column(name = "updated_at")
	    protected LocalDateTime updatedAt;

	    @Version
	    protected Long version;

	    @PrePersist
	    protected void onCreate() {
	        createdAt = LocalDateTime.now();
	    }

	    @PreUpdate
	    protected void onUpdate() {
	        updatedAt = LocalDateTime.now();
	    }
}
