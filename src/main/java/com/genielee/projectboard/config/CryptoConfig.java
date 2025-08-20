package com.genielee.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 암호화폐 관련 설정 클래스
 */
@Configuration
public class CryptoConfig {
    
    /**
     * WebClient 빌더를 빈으로 등록합니다
     * 외부 API 호출에 사용
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(this::configureCodecs)
                        .build());
    }
    
    /**
     * 코덱 설정 - 메모리 버퍼 크기 증가
     */
    private void configureCodecs(ClientCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(1024 * 1024); // 1MB
    }
}