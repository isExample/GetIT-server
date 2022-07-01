package com.example.getIt.DTO;


import com.example.getIt.entity.UserEntity;
import lombok.*;

@NoArgsConstructor
public class UserDTO {
    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    public static class User{
        private Long userIdx;
        private String email;
        private String password;
        private String name;
        private String nickName;
        private Integer birthday;
        private String job;
        private String status;

        public User(){}

        public User(UserEntity userEntity) {
            this.userIdx = userEntity.getUserIdx();
            this.name = userEntity.getName();
            this.nickName = userEntity.getNickname();
            this.birthday = userEntity.getBirthday();
            this.job = userEntity.getJob();
            this.email = userEntity.getEmail();
            this.password = userEntity.getPassword();
            this.status = userEntity.getStatus();


        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PostUserRes {
        private String jwt;
        private Long userIdx;
    }
}
