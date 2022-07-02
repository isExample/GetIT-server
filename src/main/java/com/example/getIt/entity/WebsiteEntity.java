package com.example.getIt.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "Website")
public class WebsiteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long webIdx;

    @Column(name = "cost", nullable = false, length = 20)
    private String cost;

    @Column(name = "url", nullable = false, length = 100)
    private String url;

    @ManyToOne
    @JoinColumn(name = "productIdx")
    private ProductEntity product;
}
