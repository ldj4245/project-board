package com.genielee.projectboard.service.exchange;

import com.genielee.projectboard.dto.response.BithumbTickerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 빗썸 거래소 API 서비스
 * 국내 2위 거래소 시세 정보 제공
 */
@Service
public class BithumbService {

    private final WebClient webClient;

    public BithumbService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.bithumb.com").build();
    }

    /**
     * 빗썸 특정 코인 시세 조회
     * @param coinSymbol 코인 심볼 (BTC, ETH)
     * @return 시세 정보
     */
    public Mono<BithumbTickerResponse> getTicker(String coinSymbol) {
        return webClient.get()
                .uri("/public/ticker/{symbol}_KRW", coinSymbol.toUpperCase())
                .retrieve()
                .bodyToMono(BithumbTickerResponse.class)
                .filter(response -> "0000".equals(response.status())) // 성공 응답만 필터링
                .onErrorResume(throwable -> Mono.empty());
    }

    /**
     * 빗썸 전체 시세 조회
     * @return 전체 시세 정보
     */
    public Mono<BithumbTickerResponse> getAllTickers() {
        return webClient.get()
                .uri("/public/ticker/ALL_KRW")
                .retrieve()
                .bodyToMono(BithumbTickerResponse.class)
                .filter(response -> "0000".equals(response.status()))
                .onErrorResume(throwable -> Mono.empty());
    }
}
