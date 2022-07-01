package com.example.getIt.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("phone")
public class PhoneSubEntity extends ProductEntity{

    @Column(name = "processor", nullable = false, length = 20)
    private String processor;

    @Column(name = "ram", nullable = false, length = 10)
    private String ram;
}
