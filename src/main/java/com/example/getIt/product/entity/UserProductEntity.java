package com.example.getIt.product.entity;

import com.example.getIt.product.entity.ProductEntity;
import com.example.getIt.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "userProduct")
public class UserProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userProductIdx;

    @ManyToOne
    @JoinColumn(name = "userIdx")
    private UserEntity userIdx;

    @ManyToOne
    @JoinColumn(name = "productIdx")
    private ProductEntity productIdx;

    @Builder
    public UserProductEntity(UserEntity userEntity, ProductEntity productEntity){
        this.productIdx = productEntity;
        this.userIdx = userEntity;
    }

}
