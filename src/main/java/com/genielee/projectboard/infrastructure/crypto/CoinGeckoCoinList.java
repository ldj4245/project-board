package com.genielee.projectboard.infrastructure.crypto;

import lombok.Data;

/**
 * CoinGecko API Coins List 엔드포인트 응답 데이터
 */
@Data
public class CoinGeckoCoinList {
    
    private String id;
    private String symbol;
    private String name;
}