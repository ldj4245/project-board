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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/articles", "/articles/search-hashtag").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            String registrationId = userRequest.getClientRegistration().getRegistrationId();

            if ("kakao".equals(registrationId)) {
                return processKakaoOAuth2User(userRequest, oAuth2User, userAccountService, passwordEncoder);
            } else if ("naver".equals(registrationId)) {
                return processNaverOAuth2User(userRequest, oAuth2User, userAccountService, passwordEncoder);
            }

            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        };
    }

    private OAuth2User processKakaoOAuth2User(
            OAuth2UserRequest userRequest,
            OAuth2User oAuth2User,
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(oAuth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String providerId = String.valueOf(kakaoResponse.id());

        return getOAuth2User(
                userAccountService,
                passwordEncoder,
                registrationId,
                providerId,
                kakaoResponse.email(),
                kakaoResponse.nickname()
        );
    }

    private OAuth2User processNaverOAuth2User(
            OAuth2UserRequest userRequest,
            OAuth2User oAuth2User,
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String providerId = (String) response.get("id");

        return getOAuth2User(
                userAccountService,
                passwordEncoder,
                registrationId,
                providerId,
                (String) response.get("email"),
                (String) response.get("nickname")
        );
    }

    private OAuth2User getOAuth2User(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder,
            String registrationId,
            String providerId,
            String email,
            String nickname
    ) {
        String username = registrationId + "_" + providerId;
        String password = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());

        return userAccountService.searchUser(username)
                .map(BoardPrincipal::from)
                .orElseGet(() ->
                        BoardPrincipal.from(
                                userAccountService.saveUser(
                                        username,
                                        password,
                                        email,
                                        nickname,
                                        null
                                )
                        )
                );
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService) {
        return username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}