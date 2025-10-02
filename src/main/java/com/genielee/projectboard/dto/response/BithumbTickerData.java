package com.genielee.projectboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 빗썸 시세 데이터 DTO
 */
public record BithumbTickerData(
        @JsonProperty("opening_price")
        BigDecimal openingPrice, // 시가
        
        @JsonProperty("closing_price") 
        BigDecimal closingPrice, // 현재가
        
        @JsonProperty("min_price")
        BigDecimal minPrice, // 최저가
        
        @JsonProperty("max_price")
        BigDecimal maxPrice, // 최고가
        
        @JsonProperty("units_traded")
        BigDecimal unitsTraded, // 거래량
        
        @JsonProperty("acc_trade_value")
        BigDecimal accTradeValue, // 누적 거래금액
        
        @JsonProperty("prev_closing_price")
        BigDecimal prevClosingPrice, // 전일 종가
        
        @JsonProperty("units_traded_24H")
        BigDecimal unitsTraded24H, // 24시간 거래량
        
        @JsonProperty("acc_trade_value_24H")
        BigDecimal accTradeValue24H, // 24시간 누적 거래금액
        
        @JsonProperty("fluctate_24H")
        BigDecimal fluctate24H, // 24시간 변동가
        
        @JsonProperty("fluctate_rate_24H")
        BigDecimal fluctateRate24H, // 24시간 변동률
        
        @JsonProperty("date")
        String date // 타임스탬프
) {
    
    /**
     * 변동률을 퍼센트로 계산
     */
    public BigDecimal getChangePercentage() {
        if (fluctateRate24H == null) {
            return BigDecimal.ZERO;
        }
        return fluctateRate24H;
    }
    
    /**
     * 가격 상승 여부
     */
    public boolean isPriceUp() {
        return fluctate24H != null && fluctate24H.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 가격 하락 여부  
     */
    public boolean isPriceDown() {
        return fluctate24H != null && fluctate24H.compareTo(BigDecimal.ZERO) < 0;
    }
}

