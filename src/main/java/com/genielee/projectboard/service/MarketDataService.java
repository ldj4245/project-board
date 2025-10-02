package com.genielee.projectboard.service;

import com.genielee.projectboard.dto.response.MarketPrice;
import com.genielee.projectboard.service.exchange.BinanceService;
import com.genielee.projectboard.service.exchange.BithumbService;
import com.genielee.projectboard.service.exchange.UpbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 거래소 시세 데이터 통합 서비스
 * 모든 거래소 API를 통합하여 일관된 시세 정보 제공
 */
@Slf4j
@Service
public class MarketDataService {

    private final UpbitService upbitService;
    private final BithumbService bithumbService;
    private final BinanceService binanceService;

    public MarketDataService(UpbitService upbitService, BithumbService bithumbService, BinanceService binanceService) {
        this.upbitService = upbitService;
        this.bithumbService = bithumbService;
        this.binanceService = binanceService;
    }

    /**
     * 거래소별 시세 정보 조회
     * @param exchangeName 거래소 이름 (upbit, bithumb, binance)
     * @param symbol 코인 심볼 (BTC, ETH)
     * @param quoteCurrency 기준통화 (KRW, USDT)
     * @return 시세 정보
     */
    public Mono<MarketPrice> getMarketPrice(String exchangeName, String symbol, String quoteCurrency) {
        return switch (exchangeName.toLowerCase()) {
            case "upbit" -> getUpbitPrice(symbol, quoteCurrency);
            case "bithumb" -> getBithumbPrice(symbol);
            case "binance" -> getBinancePrice(symbol, quoteCurrency);
            default -> Mono.error(new IllegalArgumentException("Unsupported exchange: " + exchangeName));
        };
    }

    /**
     * 업비트 시세 조회
     */
    private Mono<MarketPrice> getUpbitPrice(String symbol, String quoteCurrency) {
        String market = quoteCurrency.toUpperCase() + "-" + symbol.toUpperCase();
        
        return upbitService.getTickers(List.of(market))
                .next()
                .map(response -> MarketPrice.of(
                        "Upbit",
                        symbol.toUpperCase(),
                        quoteCurrency.toUpperCase(),
                        response.tradePrice(),
                        response.prevClosingPrice(),
                        response.getChangePercentage(),
                        response.accTradeVolume24h(),
                        LocalDateTime.now()
                ))
                .doOnError(error -> log.error("Upbit API error for {}: {}", symbol, error.getMessage()));
    }

    /**
     * 빗썸 시세 조회
     */
    private Mono<MarketPrice> getBithumbPrice(String symbol) {
        return bithumbService.getTicker(symbol)
                .map(response -> MarketPrice.of(
                        "Bithumb",
                        symbol.toUpperCase(),
                        "KRW",
                        response.data().closingPrice(),
                        response.data().prevClosingPrice(),
                        response.data().getChangePercentage(),
                        response.data().unitsTraded24H(),
                        LocalDateTime.now()
                ))
                .doOnError(error -> log.error("Bithumb API error for {}: {}", symbol, error.getMessage()));
    }

    /**
     * 바이낸스 시세 조회
     */
    private Mono<MarketPrice> getBinancePrice(String symbol, String quoteCurrency) {
        String binanceSymbol = symbol.toUpperCase() + quoteCurrency.toUpperCase();
        
        return binanceService.getTicker(binanceSymbol)
                .next()
                .map(response -> MarketPrice.of(
                        "Binance",
                        symbol.toUpperCase(),
                        quoteCurrency.toUpperCase(),
                        response.price(),
                        response.prevClosePrice(),
                        response.priceChangePercent(),
                        response.volume(),
                        LocalDateTime.now()
                ))
                .doOnError(error -> log.error("Binance API error for {}: {}", symbol, error.getMessage()));
    }

    /**
     * 특정 코인의 모든 거래소 시세 조회
     * @param symbol 코인 심볼
     * @return 모든 거래소 시세 리스트
     */
    public Mono<List<MarketPrice>> getAllExchangePrices(String symbol) {
        return Mono.zip(
                getUpbitPrice(symbol, "KRW").onErrorReturn(createErrorPrice("Upbit", symbol)),
                getBithumbPrice(symbol).onErrorReturn(createErrorPrice("Bithumb", symbol)),
                getBinancePrice(symbol, "USDT").onErrorReturn(createErrorPrice("Binance", symbol))
        ).map(tuple -> List.of(tuple.getT1(), tuple.getT2(), tuple.getT3()));
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
                LocalDateTime.now()
        );
    }
}
