package com.example.getIt.product.entity;

import com.example.getIt.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review")
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reveiwIdx;

    @ManyToOne
    @JoinColumn(name = "userIdx")
    private UserEntity userIdx;

    @ManyToOne
    @JoinColumn(name = "productIdx")
    private ProductEntity productIdx;

    @Column(nullable = false, length = 100)
    private String review;

    @Column(length = 500)
    private String reviewImgUrl;

    @Builder
    public ReviewEntity(UserEntity userEntity, ProductEntity productEntity, String review, String reviewImgUrl){
        this.productIdx = productEntity;
        this.userIdx = userEntity;
        this.review = review;
        this.reviewImgUrl = reviewImgUrl;
    }

}
