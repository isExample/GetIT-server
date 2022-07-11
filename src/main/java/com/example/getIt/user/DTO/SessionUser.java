package com.example.getIt.user.DTO;


import com.example.getIt.user.entity.UserEntity;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private String nickName;
    private String email;
    private String profileImgUrl;

    public SessionUser(UserEntity user){
        this.nickName = user.getNickname();
        this.email = user.getEmail();
        this.profileImgUrl = user.getProfileImgUrl();
    }
}