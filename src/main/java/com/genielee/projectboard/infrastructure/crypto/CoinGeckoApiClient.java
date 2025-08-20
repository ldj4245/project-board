package com.genielee.projectboard.infrastructure.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * CoinGecko API와의 통신을 담당하는 클라이언트
 * 실시간 암호화폐 가격 정보를 제공하는 무료 API 서비스
 */
@Slf4j
@Component
public class CoinGeckoApiClient {
    
    private final WebClient webClient;
    private final String baseUrl;
    
    public CoinGeckoApiClient(
            WebClient.Builder webClientBuilder,
            @Value("${crypto.api.coingecko.base-url:https://api.coingecko.com/api/v3}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "ProjectBoard/1.0")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }
    
    /**
     * 상위 N개 암호화폐의 현재 가격 정보를 조회합니다
     * 
     * @param limit 조회할 암호화폐 개수 (최대 250개)
     * @param vsCurrency 가격 기준 통화 (기본값: usd)
     * @return 암호화폐 가격 정보 리스트
     */
    public Mono<List<CoinGeckoMarketData>> getTopCryptocurrencies(int limit, String vsCurrency) {
        log.debug("CoinGecko API 호출 시작: 상위 {}개 암호화폐 조회 (기준통화: {})", limit, vsCurrency);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/coins/markets")
                        .queryParam("vs_currency", vsCurrency)
                        .queryParam("order", "market_cap_desc")
                        .queryParam("per_page", Math.min(limit, 250))
                        .queryParam("page", 1)
                        .queryParam("sparkline", false)
                        .queryParam("price_change_percentage", "24h")
                        .build())
                .retrieve()
                .bodyToFlux(CoinGeckoMarketData.class)
                .collectList()
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(this::isRetryableException))
                .doOnSuccess(data -> log.debug("CoinGecko API 호출 성공: {} 개 데이터 수신", data.size()))
                .doOnError(error -> log.error("CoinGecko API 호출 실패", error));
    }
    
    /**
     * 특정 암호화폐의 상세 정보를 조회합니다
     * 
     * @param coinId 코인 ID (예: "bitcoin", "ethereum")
     * @return 암호화폐 상세 정보
     */
    public Mono<CoinGeckoCoinDetail> getCoinDetail(String coinId) {
        log.debug("CoinGecko API 호출 시작: {} 상세 정보 조회", coinId);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/coins/{id}")
                        .build(coinId))
                .retrieve()
                .bodyToMono(CoinGeckoCoinDetail.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(this::isRetryableException))
                .doOnSuccess(data -> log.debug("CoinGecko API 호출 성공: {} 상세 정보 수신", coinId))
                .doOnError(error -> log.error("CoinGecko API 호출 실패: {}", coinId, error));
    }
    
    /**
     * 지원되는 암호화폐 목록을 조회합니다
     * 
     * @return 암호화폐 목록
     */
    public Mono<List<CoinGeckoCoinList>> getSupportedCoins() {
        log.debug("CoinGecko API 호출 시작: 지원되는 암호화폐 목록 조회");
        
        return webClient.get()
                .uri("/coins/list")
                .retrieve()
                .bodyToFlux(CoinGeckoCoinList.class)
                .collectList()
                .timeout(Duration.ofSeconds(15))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(this::isRetryableException))
                .doOnSuccess(data -> log.debug("CoinGecko API 호출 성공: {} 개 코인 목록 수신", data.size()))
                .doOnError(error -> log.error("CoinGecko API 호출 실패: 지원되는 암호화폐 목록", error));
    }
    
    /**
     * 특정 암호화폐들의 현재 가격을 조회합니다
     * 
     * @param coinIds 조회할 코인 ID 리스트 (최대 100개)
     * @param vsCurrencies 가격 기준 통화 (예: "usd,krw")
     * @return 코인별 가격 정보
     */
    public Mono<CoinGeckoSimplePrice> getSimplePrices(List<String> coinIds, String vsCurrencies) {
        if (coinIds.size() > 100) {
            return Mono.error(new IllegalArgumentException("최대 100개의 코인 ID만 조회 가능합니다"));
        }
        
        String joinedIds = String.join(",", coinIds);
        log.debug("CoinGecko API 호출 시작: 간단한 가격 조회 (코인: {}, 통화: {})", joinedIds, vsCurrencies);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/simple/price")
                        .queryParam("ids", joinedIds)
                        .queryParam("vs_currencies", vsCurrencies)
                        .queryParam("include_24hr_change", true)
                        .build())
                .retrieve()
                .bodyToMono(CoinGeckoSimplePrice.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(this::isRetryableException))
                .doOnSuccess(data -> log.debug("CoinGecko API 호출 성공: 간단한 가격 정보 수신"))
                .doOnError(error -> log.error("CoinGecko API 호출 실패: 간단한 가격 조회", error));
    }
    
    /**
     * API 연결 상태를 확인합니다 (Health Check)
     * 
     * @return 연결 성공 여부
     */
    public Mono<Boolean> ping() {
        log.debug("CoinGecko API 연결 상태 확인 시작");
        
        return webClient.get()
                .uri("/ping")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> response.contains("gecko"))
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(success -> log.debug("CoinGecko API 연결 상태: {}", success ? "정상" : "비정상"))
                .doOnError(error -> log.warn("CoinGecko API 연결 확인 실패", error))
                .onErrorReturn(false);
    }
    
    /**
     * 재시도 가능한 예외인지 판단합니다
     */
    private boolean isRetryableException(Throwable throwable) {
        if (throwable instanceof TimeoutException) {
            return true;
        }
        if (throwable instanceof WebClientResponseException webClientEx) {
            var statusCode = webClientEx.getStatusCode();
            return statusCode.value() == 429 || // TOO_MANY_REQUESTS
                   statusCode.value() >= 500;    // 5xx errors
        }
        return false;
    }
}