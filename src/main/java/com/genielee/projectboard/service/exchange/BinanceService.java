package com.genielee.projectboard.service.exchange;

import com.genielee.projectboard.dto.response.BinanceTickerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 바이낸스 거래소 API 서비스
 * 글로벌 1위 거래소 시세 정보 제공
 */
@Service
public class BinanceService {

    private final WebClient webClient;

    public BinanceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.binance.com").build();
    }

    /**
     * 바이낸스 시세 정보 조회
     * @param symbols 심볼 목록 (예: ["BTCUSDT", "ETHUSDT"])
     * @return 시세 정보 스트림
     */
    public Flux<BinanceTickerResponse> getTickers(List<String> symbols) {
        String symbolsParam = "[\"" + String.join("\",\"", symbols) + "\"]";

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/ticker/24hr")
                        .queryParam("symbols", symbolsParam)
                        .build())
                .retrieve()
                .bodyToFlux(BinanceTickerResponse.class)
                .onErrorResume(throwable -> Flux.empty());
    }

    /**
     * 바이낸스 단일 심볼 시세 조회
     * @param symbol 심볼 (BTCUSDT)
     * @return 시세 정보
     */
    public Flux<BinanceTickerResponse> getTicker(String symbol) {
        return getTickers(List.of(symbol));
    }

    /**
     * 바이낸스 전체 시세 조회 (상위 100개)
     * @return 전체 시세 정보
     */
    public Flux<BinanceTickerResponse> getAllTickers() {
        return webClient.get()
                .uri("/api/v3/ticker/24hr")
                .retrieve()
                .bodyToFlux(BinanceTickerResponse.class)
                .take(100) // 상위 100개만
                .onErrorResume(throwable -> Flux.empty());
    }
}
