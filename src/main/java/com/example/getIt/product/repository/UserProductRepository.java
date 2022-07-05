package com.example.getIt.product.repository;

import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.product.entity.UserProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProductRepository extends JpaRepository<UserProductEntity, Long> {

    List<UserProductEntity> findAllByUserIdx(UserEntity userIdx);
}
