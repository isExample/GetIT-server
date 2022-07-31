package com.example.getIt.user.entity;

import com.example.getIt.product.entity.ReviewEntity;
import com.example.getIt.util.BaseEntity;
import com.example.getIt.util.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = true)
    private String provider; // google

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userIdx", orphanRemoval = true)
    private List<ReviewEntity> reviews = new ArrayList<>();


    @Builder
    public UserEntity(String nickName, String email, String password, Integer birthday,
                      String job, String profileImgUrl, String provider, String status, Role role, List<ReviewEntity> reviews){
        this.nickname = nickName;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.job = job;
        this.profileImgUrl = profileImgUrl;
        this.status = status;
        this.provider = provider;
        this.role = role;
        this.reviews = reviews;
    }
    public UserEntity update(String nickname, String profileImgUrl){
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        return this;
    }

    public String getRoleKey(){
        return this.role.name();
    }
    public void changePwd(String pwd){this.password = pwd;}
    public void changeProfile(String nickname, String profileImgUrl){
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
    }
}
