package com.genielee.projectboard.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 김치프리미엄 계산 결과 DTO
 * 국내외 가격차 정보 전송용
 */
public record KimchiPremium(
        @JsonProperty("coinSymbol")
        String coinSymbol,
        
        @JsonProperty("domesticExchange")
        String domesticExchange,
        
        @JsonProperty("internationalExchange") 
        String internationalExchange,
        
        @JsonProperty("domesticPrice")
        BigDecimal domesticPrice,
        
        @JsonProperty("internationalPrice")
        BigDecimal internationalPrice,
        
        @JsonProperty("internationalPriceInKrw")
        BigDecimal internationalPriceInKrw,
        
        @JsonProperty("exchangeRate")
        BigDecimal exchangeRate,
        
        @JsonProperty("premiumPercentage")
        BigDecimal premiumPercentage,
        
        @JsonProperty("premiumAmount")
        BigDecimal premiumAmount,
        
        @JsonProperty("timestamp")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,
        
        @JsonProperty("isPositive")
        Boolean isPositive,
        
        @JsonProperty("changePercent24h")
        BigDecimal changePercent24h,
        
        @JsonProperty("volume24h")
        BigDecimal volume24h,
        
        @JsonProperty("internationalChangePercent24h")
        BigDecimal internationalChangePercent24h,
        
        @JsonProperty("internationalVolume24h")
        BigDecimal internationalVolume24h
) {
    
    public static KimchiPremium of(
            String coinSymbol,
            String domesticExchange,
            String internationalExchange,
            BigDecimal domesticPrice,
            BigDecimal internationalPrice,
            BigDecimal exchangeRate,
            BigDecimal changePercent24h,
            BigDecimal volume24h,
            BigDecimal internationalChangePercent24h,
            BigDecimal internationalVolume24h) {
        
        // 해외 가격을 원화로 환산
        BigDecimal internationalPriceInKrw = internationalPrice.multiply(exchangeRate);
        
        // 프리미엄 금액 계산
        BigDecimal premiumAmount = domesticPrice.subtract(internationalPriceInKrw);
        
        // 프리미엄 비율 계산: ((국내가격 - 해외가격) / 해외가격) * 100
        BigDecimal premiumPercentage = premiumAmount
                .divide(internationalPriceInKrw, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        
        Boolean isPositive = premiumPercentage.compareTo(BigDecimal.ZERO) > 0;
        
        return new KimchiPremium(
                coinSymbol,
                domesticExchange,
                internationalExchange,
                domesticPrice,
                internationalPrice,
                internationalPriceInKrw,
                exchangeRate,
                premiumPercentage,
                premiumAmount,
                LocalDateTime.now(),
                isPositive,
                changePercent24h,
                volume24h,
                internationalChangePercent24h,
                internationalVolume24h
        );
    }
    
    // 간단한 생성자 (오버로딩)
    public static KimchiPremium simple(
            String coinSymbol,
            String domesticExchange,
            String internationalExchange,
            BigDecimal domesticPrice,
            BigDecimal internationalPriceInKrw,
            BigDecimal premiumPercentage,
            BigDecimal changePercent24h,
            BigDecimal volume24h,
            BigDecimal internationalChangePercent24h,
            BigDecimal internationalVolume24h) {
        
        BigDecimal premiumAmount = domesticPrice.subtract(internationalPriceInKrw);
        Boolean isPositive = premiumPercentage.compareTo(BigDecimal.ZERO) > 0;
        
        return new KimchiPremium(
                coinSymbol,
                domesticExchange,
                internationalExchange,
                domesticPrice,
                null, // internationalPrice (USD)
                internationalPriceInKrw,
                null, // exchangeRate  
                premiumPercentage,
                premiumAmount,
                LocalDateTime.now(),
                isPositive,
                changePercent24h,
                volume24h,
                internationalChangePercent24h,
                internationalVolume24h
        );
    }
}
