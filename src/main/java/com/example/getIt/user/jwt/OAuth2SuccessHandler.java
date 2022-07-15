package com.example.getIt.user.jwt;

import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.user.jwt.DTO.OAuthAttributes;
import com.example.getIt.user.jwt.DTO.TokenDTO;
import com.example.getIt.user.jwt.entity.RefreshTokenEntity;
import com.example.getIt.user.jwt.repository.RefreshTokenRepository;
import com.example.getIt.user.repository.UserRepository;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import com.example.getIt.util.BaseResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

        log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);
        // 최초 로그인이라면 회원가입 처리를 한다.
        log.info("토큰 발행 시작");
        TokenDTO token = tokenProvider.generateTokenDto(authentication);
        log.info("{}", token);
        UserEntity user = this.userRepository.findByEmail((String) oAuth2User.getAttributes().get("email")).get();
        if(!user.getProvider().equals("Not_Social")){
            RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                    .key((String) oAuth2User.getAttributes().get("email"))
                    .value(token.getRefreshToken())
                    .build();
            refreshTokenRepository.save(refreshToken);
            writeTokenResponse(response, token);
        }else {
            throwMethod(response);
        }

    }

    private void throwMethod(HttpServletResponse response) throws IOException{
        BaseResponse baseResponse = new BaseResponse(BaseResponseStatus.NOT_SOCIAL);
        response.setContentType("application/json;charset=UTF-8");
        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(baseResponse));
        writer.flush();
    }

    private void writeTokenResponse(HttpServletResponse response, TokenDTO token)
            throws IOException {
        BaseResponse baseResponse = new BaseResponse(token);
        response.setContentType("application/json;charset=UTF-8");
        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(baseResponse));
        writer.flush();
    }

}
