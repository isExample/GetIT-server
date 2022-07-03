package com.example.getIt.repository;

import com.example.getIt.DTO.ProductDTO;
import com.example.getIt.entity.ProductEntity;
import com.example.getIt.entity.UserEntity;
import com.example.getIt.entity.UserProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProductRepository extends JpaRepository<UserProductEntity, Long> {

    List<UserProductEntity> findAllByUserIdx(UserEntity userIdx);
}
