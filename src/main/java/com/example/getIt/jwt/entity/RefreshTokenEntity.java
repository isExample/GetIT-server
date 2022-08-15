package com.example.getIt.jwt.entity;

import com.example.getIt.util.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
@Entity
public class RefreshTokenEntity extends BaseEntity {

    @Id
    private String keyId;
    private String value;

    public RefreshTokenEntity updateValue(String token) {
        this.value = token;
        return this;
    }

    @Builder
    public RefreshTokenEntity(String key, String value) {
        this.keyId = key;
        this.value = value;
    }
}
