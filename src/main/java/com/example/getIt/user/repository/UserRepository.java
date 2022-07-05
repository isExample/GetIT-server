package com.example.getIt.user.repository;

import com.example.getIt.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByNickname(String nickName);
    UserEntity findByEmail(String email);
    UserEntity findAllByUserIdx(Long userIdx);
}
