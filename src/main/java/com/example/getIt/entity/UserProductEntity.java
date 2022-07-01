package com.example.getIt.entity;

import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Table(name = "UserProduct")
public class UserProductEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ManyToOne
    @JoinColumn(name = "userIdx")
    private UserEntity userIdx;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ManyToOne
    @JoinColumn(name = "productIdx")
    private ProductEntity productIdx;

}
