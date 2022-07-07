package com.example.getIt.user.DTO;


import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.util.Role;
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
        private Role role;
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
