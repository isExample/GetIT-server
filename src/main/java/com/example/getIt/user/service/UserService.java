package com.example.getIt.user.service;

import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.user.DTO.UserDTO;
import com.example.getIt.product.entity.ProductEntity;
import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.product.entity.UserProductEntity;
import com.example.getIt.product.repository.ProductRepository;
import com.example.getIt.product.repository.UserProductRepository;
import com.example.getIt.user.repository.UserRepository;
import com.example.getIt.util.*;
//import com.example.getIt.util.JwtService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.getIt.util.ValidationRegex.isRegexEmail;

@Service
public class UserService {
    private UserRepository userRepository;
    private UserProductRepository userProductRepository;
    private ProductRepository productRepository;

    private JwtService jwtService;

    public UserService(UserRepository userRepository, UserProductRepository userProductRepository, ProductRepository productRepository, JwtService jwtService){
        this.userRepository = userRepository;
        this.userProductRepository = userProductRepository;
        this.productRepository = productRepository;
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
            List<UserProductEntity> products = userProductRepository.findAllByUserIdx(userEntity);
            List<ProductDTO.GetProduct> likeProduct = new ArrayList<>();

            for(UserProductEntity temp : products){
                ProductEntity likeProductInfo = productRepository.findAllByProductIdx(temp.getProductIdx().getProductIdx());
                likeProduct.add(new ProductDTO.GetProduct(
                        likeProductInfo.getProductIdx(),
                        likeProductInfo.getName(),
                        likeProductInfo.getBrand(),
                        likeProductInfo.getType(),
                        likeProductInfo.getImage(),
                        likeProductInfo.getLowestprice()
                ));
            }

            return new UserDTO.User(
                    userEntity.getUserIdx(),
                    userEntity.getEmail(),
                    userEntity.getPassword(),
                    userEntity.getNickname(),
                    userEntity.getBirthday(),
                    userEntity.getJob(),
                    userEntity.getStatus(),
                    likeProduct
            );
        }catch (Exception e){
            System.out.println("Error: "+e);
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
