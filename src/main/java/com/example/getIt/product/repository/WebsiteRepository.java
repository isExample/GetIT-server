package com.example.getIt.product.repository;

import com.example.getIt.product.entity.ProductEntity;
import com.example.getIt.product.entity.WebsiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebsiteRepository extends JpaRepository<WebsiteEntity, Long> {
    List<WebsiteEntity> findAllByProduct(ProductEntity product);

}
