package com.example.getIt.DTO;


import com.example.getIt.entity.UserEntity;
import lombok.*;

import java.util.List;

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
        private String nickName;
        private Integer birthday;
        private String job;
        private String status;
        private List<ProductDTO.GetProduct> likeProduct;
        public User(){}

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PostUserRes {
        private String jwt;
    }

}
