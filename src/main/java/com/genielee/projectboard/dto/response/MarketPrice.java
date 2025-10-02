package com.genielee.projectboard.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 거래소 시세 응답 DTO
 * 실시간 시세 데이터 전송용
 */
public record MarketPrice(
        @JsonProperty("exchange")
        String exchangeName,
        
        @JsonProperty("symbol")
        String coinSymbol,
        
        @JsonProperty("quoteCurrency") 
        String quoteCurrency,
        
        @JsonProperty("price")
        BigDecimal price,
        
        @JsonProperty("previousPrice")
        BigDecimal previousPrice,
        
        @JsonProperty("changePercent")
        BigDecimal changePercent,
        
        @JsonProperty("volume24h")
        BigDecimal volume24h,
        
        @JsonProperty("high24h")
        BigDecimal high24h,
        
        @JsonProperty("low24h")
        BigDecimal low24h,
        
        @JsonProperty("timestamp")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,
        
        @JsonProperty("isUp")
        Boolean isUp,
        
        @JsonProperty("isDown") 
        Boolean isDown
) {
    
    public static MarketPrice of(
            String exchangeName,
            String coinSymbol, 
            String quoteCurrency,
            BigDecimal price,
            LocalDateTime timestamp) {
        return new MarketPrice(
                exchangeName,
                coinSymbol,
                quoteCurrency, 
                price,
                null, // previousPrice
                null, // changePercent
                null, // volume24h
                null, // high24h
                null, // low24h
                timestamp,
                null, // isUp
                null  // isDown
        );
    }

    public static MarketPrice of(
            String exchangeName,
            String coinSymbol,
            String quoteCurrency,
            BigDecimal price,
            BigDecimal previousPrice,
            BigDecimal changePercent,
            BigDecimal volume24h,
            LocalDateTime timestamp) {
        
        Boolean isUp = previousPrice != null && price.compareTo(previousPrice) > 0;
        Boolean isDown = previousPrice != null && price.compareTo(previousPrice) < 0;
        
        return new MarketPrice(
                exchangeName,
                coinSymbol,
                quoteCurrency,
                price,
                previousPrice,
                changePercent,
                volume24h,
                null, // high24h
                null, // low24h
                timestamp,
                isUp,
                isDown
        );
    }

    // 전체 정보를 포함한 생성자
    public static MarketPrice ofComplete(
            String exchangeName,
            String coinSymbol,
            String quoteCurrency,
            BigDecimal price,
            BigDecimal previousPrice,
            BigDecimal changePercent,
            BigDecimal volume24h,
            BigDecimal high24h,
            BigDecimal low24h,
            LocalDateTime timestamp) {
        
        Boolean isUp = previousPrice != null && price.compareTo(previousPrice) > 0;
        Boolean isDown = previousPrice != null && price.compareTo(previousPrice) < 0;
        
        return new MarketPrice(
                exchangeName,
                coinSymbol,
                quoteCurrency,
                price,
                previousPrice,
                changePercent,
                volume24h,
                high24h,
                low24h,
                timestamp,
                isUp,
                isDown
        );
    }
}

