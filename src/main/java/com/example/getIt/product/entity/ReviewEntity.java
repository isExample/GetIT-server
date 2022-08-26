package com.example.getIt.product.entity;

import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.util.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review")
public class ReviewEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewIdx;

    @ManyToOne
    @JoinColumn(name = "userIdx")
    private UserEntity userIdx;

    @ManyToOne
    @JoinColumn(name = "productIdx")
    private ProductEntity productIdx;

    @Column(nullable = false, length = 100)
    private String review;

    @Builder
    public ReviewEntity(UserEntity userEntity, ProductEntity productEntity, String review){
        this.productIdx = productEntity;
        this.userIdx = userEntity;
        this.review = review;
    }

}
