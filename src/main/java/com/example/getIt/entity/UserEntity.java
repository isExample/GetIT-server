package com.example.getIt.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class UserEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

}
