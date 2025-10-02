package com.genielee.projectboard.service.exchange;

import com.genielee.projectboard.dto.response.UpbitTickerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 업비트 거래소 API 서비스
 * 국내 1위 거래소 시세 정보 제공
 */
@Service
public class UpbitService {

    private final WebClient webClient;

    public UpbitService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.upbit.com").build();
    }

    /**
     * 업비트 시세 정보 조회
     * @param markets 마켓 코드 목록 (예: ["KRW-BTC", "KRW-ETH"])
     * @return 시세 정보 스트림
     */
    public Flux<UpbitTickerResponse> getTickers(List<String> markets) {
        String marketsParam = String.join(",", markets);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/ticker")
                        .queryParam("markets", marketsParam)
                        .build())
                .retrieve()
                .bodyToFlux(UpbitTickerResponse.class)
                .onErrorResume(throwable -> {
                    // 에러 발생 시 빈 스트림 반환
                    return Flux.empty();
                });
    }

    /**
     * 업비트 마켓 정보 조회
     * @return 지원하는 마켓 목록
     */
    public Flux<Object> getMarkets() {
        return webClient.get()
                .uri("/v1/market/all?isDetails=true")
                .retrieve()
                .bodyToFlux(Object.class);
    }
}
