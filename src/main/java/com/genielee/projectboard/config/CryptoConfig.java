package com.genielee.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executor;

/**
 * 암호화폐 관련 설정 클래스
 */
@Configuration
@EnableAsync
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
     * 비동기 작업을 위한 스레드 풀 설정
     */
    @Bean(name = "cryptoTaskExecutor")
    public Executor cryptoTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("crypto-");
        executor.initialize();
        return executor;
    }
    
    /**
     * 코덱 설정 - 메모리 버퍼 크기 증가
     */
    private void configureCodecs(ClientCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(1024 * 1024); // 1MB
    }
}