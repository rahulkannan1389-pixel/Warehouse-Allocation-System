package com.techpalle.repository;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.techpalle.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Serializable>{

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Product> findAll(Pageable pageable);

	

}
