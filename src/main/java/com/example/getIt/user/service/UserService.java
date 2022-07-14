package com.example.getIt.user.service;

import com.example.getIt.user.jwt.entity.RefreshTokenEntity;
import com.example.getIt.user.jwt.repository.RefreshTokenRepository;
import com.example.getIt.user.jwt.DTO.TokenDTO;
import com.example.getIt.user.jwt.TokenProvider;
import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.user.DTO.UserDTO;
import com.example.getIt.product.entity.ProductEntity;
import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.product.entity.UserProductEntity;
import com.example.getIt.product.repository.ProductRepository;
import com.example.getIt.product.repository.UserProductRepository;
import com.example.getIt.user.repository.UserRepository;
import com.example.getIt.util.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.example.getIt.util.ValidationRegex.isRegexEmail;
import static com.example.getIt.util.ValidationRegex.isRegexPwd;

@Service
public class UserService {
    private UserRepository userRepository;
    private UserProductRepository userProductRepository;
    private ProductRepository productRepository;
    private PasswordEncoder passwordEncoder;
    private TokenProvider tokenProvider;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthenticationManagerBuilder authenticationManagerBuilder;



    public UserService(UserRepository userRepository, UserProductRepository userProductRepository,
                       ProductRepository productRepository, PasswordEncoder passwordEncoder,
                       TokenProvider tokenProvider, RefreshTokenRepository refreshTokenRepository,
                       AuthenticationManagerBuilder authenticationManagerBuilder){
        this.userRepository = userRepository;
        this.userProductRepository = userProductRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public TokenDTO signIn(UserDTO.User user) throws BaseException {
        if(user.getEmail() == null || user.getNickName() == null || user.getPassword() == null){
             throw new BaseException(BaseResponseStatus.POST_USERS_EMPTY);
        }
        if(!isRegexEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        if(!isRegexPwd(user.getPassword())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PWD);
        }
        if(isHaveEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.DUPLICATE_EMAIL);
        }
        if(isHaveNickName(user.getNickName())){
            throw new BaseException(BaseResponseStatus.DUPLICATE_NICKNAME);
        }
        String password = user.getPassword();
        try{
            String encodedPwd = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPwd);
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        UserEntity userEntity = UserEntity.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickName(user.getNickName())
                .birthday(user.getBirthday())
                .role(Role.ROLE_USER)
                .provider("Not_Social")
                .build();
        user.setPassword(password);
        userRepository.save(userEntity);
        return token(user);

    }

    public boolean isHaveNickName(String nickName) {
        return this.userRepository.existsByNickname(nickName);
    }

    public boolean isHaveEmail(String email) { return this.userRepository.existsByEmail(email); }


    public TokenDTO token(UserDTO.User user){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDTO tokenDto = tokenProvider.generateTokenDto(authentication);
        // 4. RefreshToken 저장
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        // 5. 토큰 발급
        return tokenDto;
    }



    public TokenDTO logIn(UserDTO.User user) throws BaseException{
        if(!isRegexEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }

        UserEntity userEntity = userRepository.findByEmail(user.getEmail()).get();
        if(userEntity == null){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }else{
            if(!userEntity.getProvider().equals("Not_Social")){
                throw new BaseException(BaseResponseStatus.SOCIAL);
            }
            if(passwordEncoder.matches(user.getPassword(), userEntity.getPassword())) { // 그냥 받아온 password를 넣으면 알아서 암호화해서 비교함.
                return token(user);
            }else{
                throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
            }

        }
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
                    userEntity.getRole(),
                    likeProduct
            );
        }catch (Exception e){
            System.out.println("Error: "+e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    public TokenDTO reissue(TokenDTO tokenRequestDto) { //재발급
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByKeyId(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDTO tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshTokenEntity newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return tokenDto;
    }

}
