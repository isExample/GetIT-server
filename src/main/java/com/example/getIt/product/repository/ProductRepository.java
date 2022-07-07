package com.example.getIt.product.repository;

import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductDTO.GetProduct> findByOrderByCreatedAt();
    ProductEntity findAllByProductIdx(Long productIdx);
}
