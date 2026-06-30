# Warehouse Allocation System
## Overview
Warehouse Allocation System is a Spring Boot based inventory management application that supports
- Product Management
- Warehouse Management
- Inventory Management
- Product Allocation
- Auto Allocation
- Stock Transfers
- Pagination and Filtering
- REST APIs
- Swagger Documentation
## Database Schema Script

CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);


CREATE TABLE warehouses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    location VARCHAR(255),
    capacity BIGINT NOT NULL,
    used_capacity BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(20),
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);


CREATE TABLE warehouse_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    available_quantity BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_inventory_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),
    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
);


CREATE TABLE allocations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id VARCHAR(100) UNIQUE,
    warehouse_id BIGINT,
    product_id BIGINT,
    quantity BIGINT,
    status VARCHAR(30),
    allocated_by VARCHAR(100),
    allocated_at TIMESTAMP,
    notes VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);


CREATE TABLE stock_transfers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_warehouse_id BIGINT,
    target_warehouse_id BIGINT,
    product_id BIGINT,
    quantity BIGINT,
    status VARCHAR(30),
    transfer_date TIMESTAMP,
    transferred_by VARCHAR(100),
    notes VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

## Swagger Documentation
 http://localhost:9090/swagger-ui/index.html
 ### Features Available
 
- Product APIs
= Warehouse APIs
- Inventory APIs
- Allocation APIs
- Stock Transfer APIs
## Important Requests

POST /api/v1/products

POST /api/v1/warehouses

PUT /api/v1/warehouses/{id}/activate

POST /api/v1/inventory/warehouse/{warehouseId}/product/{productId}/initialize

POST /api/v1/allocations

POST /api/v1/allocations/auto-allocate

PUT /api/v1/allocations/{id}/cancel

POST /api/v1/stock-transfers

GET /api/v1/inventory/product/{productId}/total-stock

## Unit Test Cases

- ProductServiceTest
- WarehouseServiceTest
- AllocationServiceTest

### Coverage Areas

- Product creation
- Duplicate SKU validation
- Warehouse creation
- Warehouse activation/deactivation
- Allocation success
- Allocation failure
- Auto allocation
- Stock validation
- Transfer validation
- Exception scenarios

## Assumptions & Design Decisions

### Assumptions

- Product SKU is unique.
- Warehouse name is unique.
- Warehouses are soft deleted.
- Product quantity is maintained in WarehouseInventory.
- Allocations reduce available inventory.
- Cancelling allocation restores inventory.
- Transfers can occur only between active warehouses.
- Source and target warehouses cannot be the same.
  
## Design Decisions

### Layered Architecture

 - Controller
 - Service
 - Repository
 - Database
### DTO Pattern
Request and Response DTOs are used to avoid exposing entities.

### Soft Delete
Warehouse uses deletedAt field.

### Idempotency
Allocation requestId prevents duplicate allocation requests.

### Validation
Business validations are performed in service layer.

### Transaction Management
Critical operations are wrapped with @Transactional.

### Pagination
All listing APIs support pagination using Pageable.

### Exception Handling
Centralized exception handling is implemented using GlobalExceptionHandler.

### Logging
SLF4J logging is used in controller and service layers.

### API Documentation
Swagger/OpenAPI is used to generate interactive API documentation.








