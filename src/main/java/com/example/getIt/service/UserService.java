package com.example.getIt.service;

import com.example.getIt.DTO.UserDTO;
import com.example.getIt.entity.UserEntity;
import com.example.getIt.repository.UserRepository;
import com.example.getIt.util.*;
//import com.example.getIt.util.JwtService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import static com.example.getIt.util.ValidationRegex.isRegexEmail;

@Service
public class UserService {
    private UserRepository userRepository;

    private JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService){
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }
    public UserDTO.PostUserRes signIn(UserDTO.User user) throws BaseException {
        if(user.getEmail() == null || user.getNickName() == null || user.getPassword() == null){
             throw new BaseException(BaseResponseStatus.POST_USERS_EMPTY);
        }
        if(!isRegexEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        if(isHaveEmail(user.getEmail()) != null){
            throw new BaseException(BaseResponseStatus.DUPLICATE_EMAIL);
        }
        if(isHaveNickName(user.getNickName())!= null){
            throw new BaseException(BaseResponseStatus.DUPLICATE_NICKNAME);
        }

        try{
            String pwd = new SHA256().encrypt(user.getPassword());
            user.setPassword(pwd);
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        UserEntity userEntity = UserEntity.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickName(user.getNickName())
                .birthday(user.getBirthday())
                .build();
        userRepository.save(userEntity);

        try{
            String jwt = this.jwtService.createJwt(Math.toIntExact(userEntity.getUserIdx()));
            return new UserDTO.PostUserRes(jwt);
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

    public UserDTO.User getUser(Long userIdx) throws BaseException {
        try{
            UserEntity userEntity = userRepository.findAllByUserIdx(userIdx);
            return new UserDTO.User(
                    userEntity.getUserIdx(),
                    userEntity.getEmail(),
                    userEntity.getPassword(),
                    userEntity.getNickname(),
                    userEntity.getBirthday(),
                    userEntity.getJob(),
                    userEntity.getStatus()
            );
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    public UserDTO.PostUserRes logIn(UserDTO.User user) throws BaseException{
        if(!isRegexEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        UserEntity userEntity = userRepository.findByEmail(user.getEmail());
        if(userEntity == null){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);

        }else{
            String encryptPwd;
            try{
                encryptPwd = new SHA256().encrypt(user.getPassword());
            }
            catch (Exception e){
                throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
            }
            if(userEntity.getPassword().equals(encryptPwd)) {
                String jwt = this.jwtService.createJwt(Math.toIntExact(userEntity.getUserIdx()));
                return new UserDTO.PostUserRes(jwt);
            } else throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }


    }
}
