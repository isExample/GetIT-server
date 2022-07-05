package com.example.getIt.user.entity;

import com.example.getIt.util.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor
@DynamicInsert
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userIdx;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false, length = 100)
    private String email;

    @JsonIgnore //json 불러오지 않기
    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private Integer birthday;

    @Column(nullable = true)
    private String job;

    @Column(nullable = true)
    private String profileImgUrl;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    @Builder
    public UserEntity(String nickName, String email, String password, Integer birthday, String job, String profileImgUrl, String status){
        this.nickname = nickName;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.job = job;
        this.profileImgUrl = profileImgUrl;
        this.status = status;
    }
}
