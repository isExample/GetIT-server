package com.example.getIt.user.jwt.service;

import com.example.getIt.user.DTO.SessionUser;
import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.user.jwt.DTO.OAuthAttributes;
import com.example.getIt.user.repository.UserRepository;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        UserEntity user = saveOrUpdate(attributes, registrationId);
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }


    private UserEntity saveOrUpdate(OAuthAttributes attributes, String registrationId) {
        UserEntity user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> {
                    try {
                        return change(entity, attributes, registrationId);
                    } catch (BaseException e) {
                        e.printStackTrace();
                    }
                    return entity;
                })
                .orElse(attributes.toEntity());
        return userRepository.save(user);

    }

    private UserEntity change(UserEntity user, OAuthAttributes attributes, String registrationId) throws BaseException {
        if(user.getProvider().equals(registrationId)){
            return user.update(attributes.getNickName(), attributes.getProfileImgUrl());
        }else throw new BaseException(BaseResponseStatus.DUPLICATE_EMAIL);// base Exception 제공하기

    }
}