package com.genielee.projectboard.infrastructure.crypto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * CoinGecko API Simple Price 엔드포인트 응답 데이터
 * 동적인 JSON 구조를 처리하기 위한 클래스
 */
@Data
public class CoinGeckoSimplePrice {
    
    private Map<String, CoinPrice> prices = new HashMap<>();
    
    @JsonAnySetter
    public void setPrices(String coinId, Object priceData) {
        if (priceData instanceof Map<?, ?> priceMap) {
            CoinPrice coinPrice = new CoinPrice();
            
            priceMap.forEach((currency, value) -> {
                String currencyStr = currency.toString();
                if (value instanceof Number) {
                    BigDecimal price = new BigDecimal(value.toString());
                    
                    if (currencyStr.equals("usd")) {
                        coinPrice.setUsd(price);
                    } else if (currencyStr.equals("krw")) {
                        coinPrice.setKrw(price);
                    } else if (currencyStr.equals("usd_24h_change")) {
                        coinPrice.setUsd24hChange(price);
                    } else if (currencyStr.equals("krw_24h_change")) {
                        coinPrice.setKrw24hChange(price);
                    }
                }
            });
            
            prices.put(coinId, coinPrice);
        }
    }
    
    /**
     * 특정 코인의 가격 정보를 조회합니다
     */
    public CoinPrice getCoinPrice(String coinId) {
        return prices.get(coinId);
    }
    
    /**
     * 개별 코인의 가격 정보를 담는 내부 클래스
     */
    @Data
    public static class CoinPrice {
        private BigDecimal usd;
        private BigDecimal krw;
        private BigDecimal usd24hChange;
        private BigDecimal krw24hChange;
    }
}