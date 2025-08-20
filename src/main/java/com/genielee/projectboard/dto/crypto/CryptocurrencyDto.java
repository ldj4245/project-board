package com.genielee.projectboard.dto.crypto;

import com.genielee.projectboard.domain.crypto.Cryptocurrency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 암호화폐 정보 DTO
 */
public record CryptocurrencyDto(
        String id,
        String symbol,
        String name,
        String image,
        BigDecimal currentPrice,
        BigDecimal currentPriceKrw,
        BigDecimal marketCap,
        Integer rank,
        BigDecimal priceChangePercentage24h,
        BigDecimal priceChange24h,
        BigDecimal high24h,
        BigDecimal low24h,
        BigDecimal totalVolume,
        Boolean isActive,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    
    public static CryptocurrencyDto of(String id, String symbol, String name, 
                                      BigDecimal currentPrice, BigDecimal currentPriceKrw) {
        return new CryptocurrencyDto(
                id, symbol, name, null, currentPrice, currentPriceKrw, 
                null, null, null, null, null, null, null, true,
                null, null, null, null
        );
    }
    
    public static CryptocurrencyDto from(Cryptocurrency entity) {
        return new CryptocurrencyDto(
                entity.getId(),
                entity.getSymbol(),
                entity.getName(),
                entity.getImage(),
                entity.getCurrentPrice(),
                entity.getCurrentPriceKrw(),
                entity.getMarketCap(),
                entity.getRank(),
                entity.getPriceChangePercentage24h(),
                entity.getPriceChange24h(),
                entity.getHigh24h(),
                entity.getLow24h(),
                entity.getTotalVolume(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }
    
    public Cryptocurrency toEntity() {
        var crypto = Cryptocurrency.of(id, symbol, name);
        crypto.updatePriceInfo(
                currentPrice, currentPriceKrw, marketCap, rank,
                priceChangePercentage24h, priceChange24h,
                high24h, low24h, totalVolume
        );
        if (image != null) {
            crypto.updateImage(image);
        }
        return crypto;
    }
    
    /**
     * 가격 상승 여부를 확인합니다
     */
    public boolean isPriceUp() {
        return priceChangePercentage24h != null && priceChangePercentage24h.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 가격 하락 여부를 확인합니다
     */
    public boolean isPriceDown() {
        return priceChangePercentage24h != null && priceChangePercentage24h.compareTo(BigDecimal.ZERO) < 0;
    }
}