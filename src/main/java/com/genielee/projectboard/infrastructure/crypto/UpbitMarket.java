package com.genielee.projectboard.infrastructure.crypto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Upbit API Market 엔드포인트 응답 데이터
 */
@Data
public class UpbitMarket {
    
    private String market;
    
    @JsonProperty("korean_name")
    private String koreanName;
    
    @JsonProperty("english_name")
    private String englishName;
    
    @JsonProperty("market_warning")
    private String marketWarning;
}