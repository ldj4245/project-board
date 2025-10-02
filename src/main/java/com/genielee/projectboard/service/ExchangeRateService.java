package com.genielee.projectboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

/**
 * 환율 API 서비스
 * USD -> KRW 환율 정보 제공
 */
@Slf4j
@Service
public class ExchangeRateService {
    
    private final WebClient webClient;
    private volatile BigDecimal cachedRate = BigDecimal.valueOf(1300); // 기본 환율
    private volatile long lastUpdateTime = 0;
    private static final long CACHE_DURATION_MS = 5 * 60 * 1000; // 5분 캐싱
    
    public ExchangeRateService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.exchangerate-api.com")
                .build();
    }
    
    /**
     * USD -> KRW 환율 조회 (5분 캐싱)
     */
    public Mono<BigDecimal> getUsdToKrw() {
        long currentTime = System.currentTimeMillis();
        
        // 캐시가 유효한 경우
        if (currentTime - lastUpdateTime < CACHE_DURATION_MS) {
            log.debug("Using cached exchange rate: {}", cachedRate);
            return Mono.just(cachedRate);
        }
        
        // API 호출
        return webClient.get()
                .uri("/v4/latest/USD")
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .map(response -> {
                    BigDecimal rate = response.rates().getOrDefault("KRW", cachedRate);
                    cachedRate = rate;
                    lastUpdateTime = System.currentTimeMillis();
                    log.info("Exchange rate updated: 1 USD = {} KRW", rate);
                    return rate;
                })
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(error -> {
                    log.warn("Failed to fetch exchange rate, using cached: {}", error.getMessage());
                    return Mono.just(cachedRate);
                });
    }
    
    /**
     * 환율 API 응답 DTO
     */
    private record ExchangeRateResponse(
            String result,
            String base_code,
            Map<String, BigDecimal> rates
    ) {}
}
