package com.genielee.projectboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 빗썸 시세 API 응답 DTO
 */
public record BithumbTickerResponse(
        @JsonProperty("status")
        String status, // 0000: 성공
        
        @JsonProperty("data")
        BithumbTickerData data
) {
    
    public static BithumbTickerResponse of(String status, BithumbTickerData data) {
        return new BithumbTickerResponse(status, data);
    }
}

