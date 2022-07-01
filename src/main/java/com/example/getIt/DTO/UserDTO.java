package com.example.getIt.DTO;


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
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PostUserRes {
        private String jwt;
        private Long userIdx;
    }
}
