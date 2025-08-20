package com.genielee.projectboard.infrastructure.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

/**
 * Upbit API와의 통신을 담당하는 클라이언트 (백업용)
 * 한국 원화(KRW) 기준 암호화폐 가격 정보 제공
 */
@Slf4j
@Component
public class UpbitApiClient {
    
    private final WebClient webClient;
    private final String baseUrl;
    
    public UpbitApiClient(
            WebClient.Builder webClientBuilder,
            @Value("${crypto.api.upbit.base-url:https://api.upbit.com/v1}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "ProjectBoard/1.0")
                .build();
    }
    
    /**
     * Upbit 마켓 코드 목록을 조회합니다
     * 
     * @return 마켓 코드 목록
     */
    public Mono<List<UpbitMarket>> getMarkets() {
        log.debug("Upbit API 호출 시작: 마켓 코드 목록 조회");
        
        return webClient.get()
                .uri("/market/all")
                .retrieve()
                .bodyToFlux(UpbitMarket.class)
                .collectList()
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .doOnSuccess(data -> log.debug("Upbit API 호출 성공: {} 개 마켓 수신", data.size()))
                .doOnError(error -> log.error("Upbit API 호출 실패: 마켓 목록", error));
    }
    
    /**
     * 현재가 정보를 조회합니다
     * 
     * @param markets 조회할 마켓 코드 리스트 (예: "KRW-BTC,KRW-ETH")
     * @return 현재가 정보 리스트
     */
    public Mono<List<UpbitTicker>> getTickers(String markets) {
        log.debug("Upbit API 호출 시작: 현재가 조회 (마켓: {})", markets);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ticker")
                        .queryParam("markets", markets)
                        .build())
                .retrieve()
                .bodyToFlux(UpbitTicker.class)
                .collectList()
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .doOnSuccess(data -> log.debug("Upbit API 호출 성공: {} 개 현재가 정보 수신", data.size()))
                .doOnError(error -> log.error("Upbit API 호출 실패: 현재가 조회", error));
    }
    
    /**
     * KRW 마켓의 주요 암호화폐 현재가를 조회합니다
     * 
     * @return KRW 마켓 현재가 정보
     */
    public Mono<List<UpbitTicker>> getKrwMarketTickers() {
        // 주요 KRW 마켓 코인들
        String majorKrwMarkets = "KRW-BTC,KRW-ETH,KRW-XRP,KRW-ADA,KRW-DOT,KRW-LINK,KRW-LTC,KRW-BCH,KRW-EOS,KRW-TRX";
        return getTickers(majorKrwMarkets);
    }
}