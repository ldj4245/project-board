package com.genielee.projectboard.config;

import com.genielee.projectboard.dto.UserAccountDto;
import com.genielee.projectboard.dto.security.BoardPrincipal;
import com.genielee.projectboard.dto.security.KakaoOAuth2Response;
import com.genielee.projectboard.repository.UserAccountRepository;
import com.genielee.projectboard.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig  {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                )
                .build();
    }


    //우리는 카카오 로그인 성공시 db에 넣어줘야 하므로 userAccountService를 이용한다.
    //그리고 비밀번호는 암호화하여 따로 저장한다.
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ){

        //기본 구현체 제공
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest); //결과 받을 객체

            //우리가 만든 객체로 매핑
            KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(oAuth2User.getAttributes());

            //고유값 만들어 주기
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String providerId = String.valueOf(kakaoResponse.id());
            String username = registrationId + "_" + providerId;
            String password = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID().toString());

            return userAccountService.searchUser(username)
                    .map(BoardPrincipal::from)
                    //카카오 인증정보 없을 경우
                    .orElseGet(() ->
                        BoardPrincipal.from(
                                userAccountService.saveUser(
                                        username,
                                        password,
                                        kakaoResponse.email(),
                                        kakaoResponse.nickname(),
                                        null
                                )

                        )
                    );
        };
    }

    //실제 인증 데이터를 가져오는
    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService){
        return username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username));
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
