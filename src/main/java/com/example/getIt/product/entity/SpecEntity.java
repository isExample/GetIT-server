package com.example.getIt.product.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "spec")
@NoArgsConstructor
public class SpecEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Idx;

    @Column(nullable = false, length = 20)
    private String type;
    @Column(nullable = false, length = 20)
    private String foruse;
    @Column(nullable = false, length = 150)
    private String forprice;
    @Column(length = 20)
    private String job;
    @Column(length = 150)
    private String productImg;
    @Column(length = 100)
    private String productName;
    @Column(length = 20)
    private String brand;
    @Column(length = 20)
    private String price;
    @Column(length = 20)
    private String productId;

    @Builder
    public SpecEntity(String type, String foruse, String forprice, String job, String productImg, String productName, String brand, String price, String productId){
        this.type = type;
        this.foruse = foruse;
        this.forprice = forprice;
        this.job = job;
        this.productImg = productImg;
        this.productName = productName;
        this.brand = brand;
        this.price = price;
        this.productId = productId;
    }
}
