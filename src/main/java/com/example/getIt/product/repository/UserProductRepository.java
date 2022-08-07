package com.example.getIt.product.repository;

import com.example.getIt.product.entity.ProductEntity;
import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.product.entity.UserProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserProductRepository extends JpaRepository<UserProductEntity, Long> {

    List<UserProductEntity> findAllByUserIdx(UserEntity userIdx);
    UserProductEntity findAllByUserIdxAndProductIdx(UserEntity userIdx, ProductEntity productIdx);
    @Transactional
    void deleteByUserProductIdx(Long userProductIdx);
}
