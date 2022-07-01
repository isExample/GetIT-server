package com.example.getIt.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@DiscriminatorValue("computer")
public class ComputerSubEntity extends ProductEntity{

    @Column(name = "cpu", nullable = false, length = 20)
    private String cpu;

    @Column(name = "ram", nullable = false, length = 10)
    private String ram;
}
