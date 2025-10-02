package com.genielee.projectboard.controller;

import com.genielee.projectboard.dto.response.KimchiPremium;
import com.genielee.projectboard.service.PremiumCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 김치 프리미엄 REST API 컨트롤러
 * 국내외 가격 차이 정보 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/premium")
@RequiredArgsConstructor
public class PremiumController {
    
    private final PremiumCalculationService premiumService;
    
    /**
     * 특정 코인의 김치 프리미엄 조회
     * GET /api/premium/BTC
     */
    @GetMapping("/{coinSymbol}")
    public Mono<KimchiPremium> getPremium(@PathVariable String coinSymbol) {
        log.info("Kimchi premium request for: {}", coinSymbol);
        return premiumService.calculatePremium(coinSymbol);
    }
    
    /**
     * 주요 코인들의 프리미엄 조회
     * GET /api/premium/top
     */
    @GetMapping("/top")
    public Mono<List<KimchiPremium>> getTopCoinsPremium() {
        log.info("Top coins kimchi premium request");
        return premiumService.calculateTopCoinsPremium();
    }
    
    /**
     * 여러 코인의 프리미엄 한번에 조회
     * GET /api/premium/batch?symbols=BTC,ETH,XRP
     */
    @GetMapping("/batch")
    public Flux<KimchiPremium> getBatchPremium(@RequestParam String symbols) {
        List<String> symbolList = List.of(symbols.split(","));
        log.info("Batch kimchi premium request for: {}", symbolList);
        
        return Flux.fromIterable(symbolList)
                .flatMap(premiumService::calculatePremium);
    }
    
    /**
     * 여러 코인의 프리미엄 한번에 조회 (POST)
     * POST /api/premium/batch
     * Body: ["BTC", "ETH", "XRP"]
     */
    @PostMapping("/batch")
    public Mono<List<KimchiPremium>> getBatchPremiumPost(@RequestBody List<String> symbols) {
        log.info("Batch kimchi premium request (POST) for: {}", symbols);
        
        return Flux.fromIterable(symbols)
                .flatMap(premiumService::calculatePremium)
                .collectList();
    }
    
    /**
     * 역프리미엄 여부 확인
     * GET /api/premium/BTC/negative
     */
    @GetMapping("/{coinSymbol}/negative")
    public Mono<Boolean> isNegativePremium(@PathVariable String coinSymbol) {
        return premiumService.isNegativePremium(coinSymbol);
    }
    
    /**
     * 코인 유효성 검증 (Upbit과 Binance에 존재하는지 확인)
     * GET /api/premium/validate/{coinSymbol}
     */
    @GetMapping("/validate/{coinSymbol}")
    public Mono<Boolean> validateCoin(@PathVariable String coinSymbol) {
        log.info("Validating coin: {}", coinSymbol);
        return premiumService.validateCoin(coinSymbol);
    }
    
    /**
     * API 상태 확인
     */
    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("Kimchi Premium API is running");
    }
}
