package com.example.getIt.product.entity;

import com.example.getIt.util.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productIdx;

    @Column(nullable = false, length = 20)
    private String productId;
    @Column(nullable = false, length = 150)
    private String productUrl;
    @Column(length = 150)
    private String image;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(length = 50)
    private String brand;
    @Column(length = 50)
    private String date;
    @Column(length = 50)
    private String cpu;
    @Column(length = 50)
    private String cpurate;
    @Column(length = 50)
    private String core;
    @Column(length = 50)
    private String size;
    @Column(length = 50)
    private String ram;
    @Column(length = 50)
    private String weight;
    @Column(length = 50)
    private String type;
    @Column(length = 50)
    private String innermemory; // 내장메모리
    @Column(length = 50)
    private String communication; // 통신 규격
    @Column(length = 50)
    private String os; // 운영 체제
    @Column(length = 50)
    private String ssd;
    @Column(length = 50)
    private String hdd;
    @Column(length = 50)
    private String output; // 출력
    @Column(length = 50)
    private String terminal; // 단자
    @Column(length = 100)
    private String description;
    @Column(length = 20)
    private String lowestprice;

    @Builder
    public ProductEntity(String productId, String productUrl, String image, String name,
                         String brand, String date, String cpu, String cpurate, String core, String ram, String size,
                         String weight, String type, String innermemory, String communication, String os, String ssd, String hdd, String output, String terminal){
        this.productId = productId;
        this.productUrl = productUrl;
        this.type = type;
        this.brand = brand;
        this.weight = weight;
        this.cpu = cpu;
        this.cpurate = cpurate;
        this.core = core;
        this.os = os;
        this.ram = ram;
        this.size = size;
        this.innermemory = innermemory;
        this.communication = communication;
        this.ssd = ssd;
        this.hdd = hdd;
        this.output = output;
        this.terminal = terminal;
        this.image = image;
        this.date = date;
        this.name = name;
        this.description = description;
        this.lowestprice = lowestprice;
    }
}
