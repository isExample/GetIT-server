package com.example.getIt.product.entity;

import com.example.getIt.util.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productIdx;

    @Column(nullable = false, length = 10)
    private String productId;

    @Column(nullable = false, length = 10)
    private String productUrl;

    @Column(nullable = false, length = 10)
    private String type;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 20)
    private String brand;

    @Column(nullable = false, length = 50)
    private String image;

    @Column(nullable = false, length = 10)
    private String date;

    @Column(length = 100)
    private String description;

    @Column(length = 20)
    private String lowestprice;

    @Column(nullable = false, length = 20)
    private String cpu;

    @Column(nullable = false, length = 10)
    private String ram;

    @Builder
    public ProductEntity(String productId, String productUrl, String type, String brand, String image, String date,
                         String description, String lowestprice, String cpu, String ram, String name){
        this.productId = productId;
        this.productUrl = productUrl;
        this.type = type;
        this.brand = brand;
        this.image = image;
        this.date = date;
        this.name = name;
        this.description = description;
        this.lowestprice = lowestprice;
        this.cpu = cpu;
        this.ram = ram;
    }
}
