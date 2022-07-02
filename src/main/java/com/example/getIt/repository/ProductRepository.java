package com.example.getIt.repository;

import com.example.getIt.DTO.ProductDTO;
import com.example.getIt.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductDTO.GetProduct> findByOrderByCreatedAt();
}
