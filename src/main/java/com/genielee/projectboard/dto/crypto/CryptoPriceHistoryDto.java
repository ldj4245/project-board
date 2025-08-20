package com.genielee.projectboard.dto.crypto;

import com.genielee.projectboard.domain.crypto.CryptoPriceHistory;
import com.genielee.projectboard.domain.crypto.CryptoPriceHistory.TimeFrame;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 암호화폐 가격 이력 DTO
 */
public record CryptoPriceHistoryDto(
        Long id,
        String cryptocurrencyId,
        String cryptocurrencySymbol,
        String cryptocurrencyName,
        BigDecimal price,
        BigDecimal priceKrw,
        BigDecimal volume,
        BigDecimal marketCap,
        LocalDateTime recordedAt,
        TimeFrame timeFrame,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    
    public static CryptoPriceHistoryDto of(String cryptocurrencyId, BigDecimal price, 
                                          BigDecimal priceKrw, TimeFrame timeFrame) {
        return new CryptoPriceHistoryDto(
                null, cryptocurrencyId, null, null, price, priceKrw, 
                null, null, LocalDateTime.now(), timeFrame,
                null, null, null, null
        );
    }
    
    public static CryptoPriceHistoryDto from(CryptoPriceHistory entity) {
        return new CryptoPriceHistoryDto(
                entity.getId(),
                entity.getCryptocurrency().getId(),
                entity.getCryptocurrency().getSymbol(),
                entity.getCryptocurrency().getName(),
                entity.getPrice(),
                entity.getPriceKrw(),
                entity.getVolume(),
                entity.getMarketCap(),
                entity.getRecordedAt(),
                entity.getTimeFrame(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }
}