package com.genielee.projectboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 바이낸스 24시간 시세 API 응답 DTO
 */
public record BinanceTickerResponse(
        @JsonProperty("symbol")
        String symbol, // BTCUSDT
        
        @JsonProperty("priceChange")
        BigDecimal priceChange, // 24시간 가격 변동
        
        @JsonProperty("priceChangePercent")
        BigDecimal priceChangePercent, // 24시간 변동률
        
        @JsonProperty("weightedAvgPrice")
        BigDecimal weightedAvgPrice, // 가중 평균가
        
        @JsonProperty("prevClosePrice")
        BigDecimal prevClosePrice, // 전일 종가
        
        @JsonProperty("lastPrice")
        BigDecimal lastPrice, // 현재가
        
        @JsonProperty("lastQty")
        BigDecimal lastQty,
        
        @JsonProperty("bidPrice")
        BigDecimal bidPrice, // 매수호가
        
        @JsonProperty("bidQty")
        BigDecimal bidQty,
        
        @JsonProperty("askPrice")
        BigDecimal askPrice, // 매도호가
        
        @JsonProperty("askQty")
        BigDecimal askQty,
        
        @JsonProperty("openPrice")
        BigDecimal openPrice, // 시가
        
        @JsonProperty("highPrice")
        BigDecimal highPrice, // 24시간 최고가
        
        @JsonProperty("lowPrice")
        BigDecimal lowPrice, // 24시간 최저가
        
        @JsonProperty("volume")
        BigDecimal volume, // 24시간 거래량
        
        @JsonProperty("quoteVolume")
        BigDecimal quoteVolume, // 24시간 거래금액
        
        @JsonProperty("openTime")
        Long openTime,
        
        @JsonProperty("closeTime")
        Long closeTime,
        
        @JsonProperty("firstId")
        Long firstId,
        
        @JsonProperty("lastId")
        Long lastId,
        
        @JsonProperty("count")
        Long count // 거래 횟수
) {
    
    /**
     * 코인 심볼 추출 (BTCUSDT -> BTC)
     */
    public String getCoinSymbol() {
        if (symbol == null) return null;
        
        // USDT 페어인 경우
        if (symbol.endsWith("USDT")) {
            return symbol.substring(0, symbol.length() - 4);
        }
        // BUSD 페어인 경우  
        if (symbol.endsWith("BUSD")) {
            return symbol.substring(0, symbol.length() - 4);
        }
        // BTC 페어인 경우
        if (symbol.endsWith("BTC")) {
            return symbol.substring(0, symbol.length() - 3);
        }
        // ETH 페어인 경우
        if (symbol.endsWith("ETH")) {
            return symbol.substring(0, symbol.length() - 3);
        }
        
        return symbol;
    }
    
    /**
     * 기준통화 추출 (BTCUSDT -> USDT)
     */
    public String getQuoteCurrency() {
        if (symbol == null) return "USDT";
        
        if (symbol.endsWith("USDT")) return "USDT";
        if (symbol.endsWith("BUSD")) return "BUSD";
        if (symbol.endsWith("BTC")) return "BTC";
        if (symbol.endsWith("ETH")) return "ETH";
        
        return "USDT";
    }
    
    /**
     * 가격 상승 여부
     */
    public boolean isPriceUp() {
        return priceChange != null && priceChange.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 가격 하락 여부
     */
    public boolean isPriceDown() {
        return priceChange != null && priceChange.compareTo(BigDecimal.ZERO) < 0;
    }
    
    /**
     * 현재가 반환 (lastPrice 사용)
     */
    public BigDecimal price() {
        return lastPrice;
    }
}

