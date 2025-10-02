package com.genielee.projectboard.service;

import com.genielee.projectboard.dto.response.KimchiPremium;
import com.genielee.projectboard.dto.response.MarketPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 김치 프리미엄 계산 서비스
 * 국내외 거래소 가격 차이 분석
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumCalculationService {
    
    private final MarketDataService marketDataService;
    private final ExchangeRateService exchangeRateService;
    
    /**
     * 김치 프리미엄 계산
     * @param coinSymbol 코인 심볼 (BTC, ETH 등)
     * @return 프리미엄 정보 (%, 국내가, 해외가, 환율)
     */
    public Mono<KimchiPremium> calculatePremium(String coinSymbol) {
        return Mono.zip(
            marketDataService.getMarketPrice("upbit", coinSymbol, "KRW"), // 국내 (업비트)
            marketDataService.getMarketPrice("binance", coinSymbol, "USDT"), // 해외 (바이낸스)
            exchangeRateService.getUsdToKrw() // 환율
        ).map(tuple -> {
            MarketPrice domestic = tuple.getT1();
            MarketPrice foreign = tuple.getT2();
            BigDecimal exchangeRate = tuple.getT3();
            
            // 해외가를 원화로 환산
            BigDecimal foreignPriceKrw = foreign.price()
                    .multiply(exchangeRate)
                    .setScale(0, RoundingMode.HALF_UP);
            
            // 김치 프리미엄 계산: ((국내가 - 해외가) / 해외가) * 100
            BigDecimal premiumPercentage = domestic.price()
                    .subtract(foreignPriceKrw)
                    .divide(foreignPriceKrw, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            // 24시간 변동률 (국내 거래소 기준)
            BigDecimal changePercent24h = domestic.changePercent() != null 
                    ? domestic.changePercent() 
                    : BigDecimal.ZERO;
            
            // 24시간 거래량 (국내 거래소 기준)
            BigDecimal volume24h = domestic.volume24h() != null 
                    ? domestic.volume24h() 
                    : BigDecimal.ZERO;
            
            // 해외 거래소 24시간 변동률
            BigDecimal internationalChangePercent24h = foreign.changePercent() != null 
                    ? foreign.changePercent() 
                    : BigDecimal.ZERO;
            
            // 해외 거래소 24시간 거래량
            BigDecimal internationalVolume24h = foreign.volume24h() != null 
                    ? foreign.volume24h() 
                    : BigDecimal.ZERO;
            
            log.info("Kimchi Premium for {}: {}% (Domestic: {}원, Foreign: {}원, Domestic Change: {}%, Domestic Volume: {}, International Change: {}%, International Volume: {})", 
                    coinSymbol, premiumPercentage, domestic.price(), foreignPriceKrw, 
                    changePercent24h, volume24h, internationalChangePercent24h, internationalVolume24h);
            
            return KimchiPremium.of(
                    coinSymbol,
                    "Upbit",
                    "Binance",
                    domestic.price(),
                    foreign.price(),
                    exchangeRate,
                    changePercent24h,
                    volume24h,
                    internationalChangePercent24h,
                    internationalVolume24h
            );
        }).onErrorResume(error -> {
            log.error("Failed to calculate kimchi premium for {}: {}", coinSymbol, error.getMessage());
            return Mono.empty();
        });
    }
    
    /**
     * 주요 코인들의 김치 프리미엄 조회
     */
    public Mono<List<KimchiPremium>> calculateTopCoinsPremium() {
        List<String> topCoins = List.of("BTC", "ETH", "XRP", "SOL", "ADA");
        
        return reactor.core.publisher.Flux.fromIterable(topCoins)
                .flatMap(this::calculatePremium)
                .collectList();
    }
    
    /**
     * 역프리미엄 여부 확인
     */
    public Mono<Boolean> isNegativePremium(String coinSymbol) {
        return calculatePremium(coinSymbol)
                .map(premium -> !premium.isPositive())
                .defaultIfEmpty(false);
    }

    /**
     * 코인 유효성 검증 (Upbit과 Binance에 존재하는지 확인)
     */
    public Mono<Boolean> validateCoin(String coinSymbol) {
        return Mono.zip(
            marketDataService.getMarketPrice("upbit", coinSymbol, "KRW")
                    .map(price -> true)
                    .onErrorReturn(false),
            marketDataService.getMarketPrice("binance", coinSymbol, "USDT")
                    .map(price -> true)
                    .onErrorReturn(false)
        ).map(tuple -> tuple.getT1() && tuple.getT2()) // 둘 다 true일 때만 유효
         .onErrorReturn(false);
    }
}
