package com.genielee.projectboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 업비트 시세 API 응답 DTO
 */
public record UpbitTickerResponse(
        @JsonProperty("market")
        String market, // KRW-BTC
        
        @JsonProperty("trade_date")
        String tradeDate,
        
        @JsonProperty("trade_time")
        String tradeTime,
        
        @JsonProperty("trade_date_kst")
        String tradeDateKst,
        
        @JsonProperty("trade_time_kst")
        String tradeTimeKst,
        
        @JsonProperty("trade_timestamp")
        Long tradeTimestamp,
        
        @JsonProperty("opening_price")
        BigDecimal openingPrice, // 시가
        
        @JsonProperty("high_price")
        BigDecimal highPrice, // 고가
        
        @JsonProperty("low_price")
        BigDecimal lowPrice, // 저가
        
        @JsonProperty("trade_price")
        BigDecimal tradePrice, // 현재가
        
        @JsonProperty("prev_closing_price")
        BigDecimal prevClosingPrice, // 전일 종가
        
        @JsonProperty("change")
        String change, // RISE, FALL, EVEN
        
        @JsonProperty("change_price")
        BigDecimal changePrice, // 변화 금액
        
        @JsonProperty("change_rate")
        BigDecimal changeRate, // 변화율
        
        @JsonProperty("signed_change_price")
        BigDecimal signedChangePrice,
        
        @JsonProperty("signed_change_rate")
        BigDecimal signedChangeRate,
        
        @JsonProperty("trade_volume")
        BigDecimal tradeVolume, // 거래량
        
        @JsonProperty("acc_trade_price")
        BigDecimal accTradePrice, // 누적 거래대금
        
        @JsonProperty("acc_trade_price_24h")
        BigDecimal accTradePrice24h, // 24시간 누적 거래대금
        
        @JsonProperty("acc_trade_volume")
        BigDecimal accTradeVolume, // 누적 거래량
        
        @JsonProperty("acc_trade_volume_24h")
        BigDecimal accTradeVolume24h, // 24시간 누적 거래량
        
        @JsonProperty("highest_52_week_price")
        BigDecimal highest52WeekPrice, // 52주 최고가
        
        @JsonProperty("highest_52_week_date")
        String highest52WeekDate,
        
        @JsonProperty("lowest_52_week_price")
        BigDecimal lowest52WeekPrice, // 52주 최저가
        
        @JsonProperty("lowest_52_week_date")
        String lowest52WeekDate,
        
        @JsonProperty("timestamp")
        Long timestamp
) {
    
    /**
     * 코인 심볼 추출 (KRW-BTC -> BTC)
     */
    public String getCoinSymbol() {
        if (market == null || !market.contains("-")) {
            return market;
        }
        return market.split("-")[1];
    }
    
    /**
     * 기준통화 추출 (KRW-BTC -> KRW)
     */
    public String getQuoteCurrency() {
        if (market == null || !market.contains("-")) {
            return "KRW";
        }
        return market.split("-")[0];
    }
    
    /**
     * 변화율을 퍼센트로 변환 (0.0234 -> 2.34)
     */
    public BigDecimal getChangePercentage() {
        return changeRate != null ? 
            changeRate.multiply(new BigDecimal("100")) : 
            BigDecimal.ZERO;
    }
    
    /**
     * 가격 상승 여부
     */
    public boolean isPriceUp() {
        return "RISE".equals(change);
    }
    
    /**
     * 가격 하락 여부
     */
    public boolean isPriceDown() {
        return "FALL".equals(change);
    }
}
