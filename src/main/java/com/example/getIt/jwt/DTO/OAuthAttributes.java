package com.example.getIt.jwt.DTO;

import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.util.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String nickName;
    private String email;
    private String profileImgUrl;
    private String provider;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String nickName, String email, String profileImgUrl,
                           String provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.nickName = nickName;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
        this.provider = provider;
    }


    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        switch (registrationId) {
            case "google":
                return ofGoogle(registrationId, userNameAttributeName, attributes);
            case "kakao":
                return ofKakao(registrationId, "email", attributes);
            case "naver":
                return ofNaver(registrationId, "id", attributes);
            default:
                throw new RuntimeException();
        }
    }

    

    private static OAuthAttributes ofGoogle(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nickName((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profileImgUrl((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider(registrationId)
                .build();
    }
    private static OAuthAttributes ofKakao(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        // 카카오 로그인 생성 후 변경 필요
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .nickName((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .profileImgUrl((String)kakaoProfile.get("profile_image_url"))
                .attributes(kakaoAccount)
                .nameAttributeKey(userNameAttributeName)
                .provider(registrationId)
                .build();
    }

    private static OAuthAttributes ofNaver(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        // 네이버 로그인 생성 후 변경 필요
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .nickName((String) response.get("name"))
                .email((String) response.get("email"))
                .profileImgUrl((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .provider(registrationId)
                .build();
    }

    public UserEntity toEntity() {
        return UserEntity.builder()
                .nickName(nickName)
                .email(email)
                .password(bCryptPasswordEncoder.encode("1234"))
                .profileImgUrl(profileImgUrl)
                .provider(provider)
                .role(Role.ROLE_USER)
                .build();
    }
}