package com.genielee.projectboard.controller;

import com.genielee.projectboard.dto.response.MarketPrice;
import com.genielee.projectboard.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 실시간 시세 정보 REST API 컨트롤러
 * 프론트엔드에서 AJAX/WebSocket으로 호출
 */
@Slf4j
@RestController
@RequestMapping("/api/crypto")
public class MarketDataRestController {

    private final MarketDataService marketDataService;

    public MarketDataRestController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    /**
     * 특정 거래소의 코인 시세 조회
     * GET /api/crypto/price?exchange=upbit&symbol=BTC&quote=KRW
     */
    @GetMapping("/price")
    public Mono<MarketPrice> getPrice(
            @RequestParam String exchange,
            @RequestParam String symbol,
            @RequestParam(defaultValue = "KRW") String quote) {
        
        log.info("Price request: exchange={}, symbol={}, quote={}", exchange, symbol, quote);
        
        return marketDataService.getMarketPrice(exchange, symbol, quote);
    }

    /**
     * 특정 코인의 모든 거래소 시세 조회
     * GET /api/crypto/prices/BTC
     */
    @GetMapping("/prices/{symbol}")
    public Mono<List<MarketPrice>> getAllPrices(@PathVariable String symbol) {
        log.info("All prices request for symbol: {}", symbol);
        
        return marketDataService.getAllExchangePrices(symbol);
    }

    /**
     * 주요 코인들의 요약 정보 조회
     * GET /api/crypto/summary
     */
    @GetMapping("/summary")
    public Mono<List<MarketPrice>> getSummary() {
        List<String> majorCoins = List.of("BTC", "ETH", "XRP", "ADA", "SOL");
        
        return Mono.fromCallable(() -> majorCoins)
                .flatMapMany(coins -> reactor.core.publisher.Flux.fromIterable(coins))
                .flatMap(symbol -> marketDataService.getMarketPrice("upbit", symbol, "KRW")
                        .onErrorReturn(createErrorPrice("Upbit", symbol)))
                .collectList();
    }

    /**
     * API 상태 확인
     * GET /api/crypto/health
     */
    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("Crypto API is running");
    }

    /**
     * 에러 발생 시 기본 MarketPrice 생성
     */
    private MarketPrice createErrorPrice(String exchangeName, String symbol) {
        return MarketPrice.of(
                exchangeName,
                symbol.toUpperCase(),
                "ERROR",
                java.math.BigDecimal.ZERO,
                java.time.LocalDateTime.now()
        );
    }
}
