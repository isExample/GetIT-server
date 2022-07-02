package com.example.getIt.repository;

import com.example.getIt.DTO.ProductDTO;
import com.example.getIt.entity.ProductEntity;
import com.example.getIt.entity.WebsiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebsiteRepository extends JpaRepository<WebsiteEntity, Long> {
    List<WebsiteEntity> findAllByProduct(ProductEntity product);

}
