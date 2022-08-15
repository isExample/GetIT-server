package com.example.getIt.config;

import com.example.getIt.jwt.OAuth2SuccessHandler;
import com.example.getIt.jwt.exception.JwtAccessDeniedHandler;
import com.example.getIt.jwt.exception.JwtAuthenticationEntryPoint;
import com.example.getIt.jwt.config.JwtSecurityConfig;
import com.example.getIt.jwt.TokenProvider;
import com.example.getIt.jwt.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
    private final RedisTemplate redisTemplate;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CSRF 설정 Disable
        http.csrf().disable()
                .cors().disable()
                .headers().frameOptions().disable()
                .and()
                // exception handling 할 때 우리가 만든 클래스를 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // h2-console 을 위한 설정을 추가
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 시큐리티는 기본적으로 세션을 사용
                // 여기서는 세션을 사용하지 않기 때문에 세션 설정을 Stateless 로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 로그인, 회원가입 API 는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
                .and()
                .authorizeRequests()
                .antMatchers("/users/login").permitAll()
                .antMatchers("/users/sign-in").permitAll()
                .antMatchers("/products/{productIdx}").permitAll()
                .antMatchers("/products/all").permitAll()
                .antMatchers("/products/category").permitAll()
                .antMatchers("/products/comparison/{productIdx1}/{productIdx2}").permitAll()
                .antMatchers("/products/list").permitAll()
                .antMatchers("/products/recommend").permitAll()
                .antMatchers("/api/search").permitAll()
                .anyRequest().authenticated()  // 나머지 API 는 전부 인증 필요


                // JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스를 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider, redisTemplate))

                .and() /* OAuth */
                .oauth2Login()
                .successHandler(successHandler)
                .userInfoEndpoint() // OAuth2 로그인 성공 후 가져올 설정들
                .userService(customOAuth2UserService);

    }
}