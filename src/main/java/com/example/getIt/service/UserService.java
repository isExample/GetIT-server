package com.example.getIt.service;

import com.example.getIt.DTO.UserDTO;
import com.example.getIt.entity.UserEntity;
import com.example.getIt.repository.UserRepository;
import com.example.getIt.util.BaseException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class UserService {
    private UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public UserEntity signIn(UserDTO.User user) throws BaseException {
        UserEntity userEntity = UserEntity.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .nickName(user.getNickName())
                .birthday(user.getBirthday())
                .build();
        userRepository.save(userEntity);
        return userEntity;
    }
}
