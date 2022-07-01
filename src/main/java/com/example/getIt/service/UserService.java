package com.example.getIt.service;

import com.example.getIt.DTO.UserDTO;
import com.example.getIt.entity.UserEntity;
import com.example.getIt.repository.UserRepository;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponseStatus;
//import com.example.getIt.util.JwtService;
import com.example.getIt.util.JwtService;
import com.example.getIt.util.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class UserService {
    private UserRepository userRepository;

    private JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService){
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }
    public UserDTO.PostUserRes signIn(UserDTO.User user) throws BaseException {

        try{
            String pwd = new SHA256().encrypt(user.getPassword());
            user.setPassword(pwd);
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        UserEntity userEntity = UserEntity.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .nickName(user.getNickName())
                .birthday(user.getBirthday())
                .build();
        userRepository.save(userEntity);

        try{
            String jwt = this.jwtService.createJwt(Math.toIntExact(userEntity.getUserIdx()));
            return new UserDTO.PostUserRes(jwt, userEntity.getUserIdx());
        }catch(Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    public UserEntity isHaveNickName(String nickName) {
        return this.userRepository.findByNickname(nickName);
    }

    public UserEntity isHaveEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public UserDTO.User getUser(Long userIdx) {
        UserEntity userEntity = userRepository.findAllByUserIdx(userIdx);
        return new UserDTO.User(userEntity);
    }
}
