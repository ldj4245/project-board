package com.genielee.projectboard.dto.crypto;

import com.genielee.projectboard.domain.crypto.CryptoCategory;

import java.time.LocalDateTime;

/**
 * 암호화폐 카테고리 DTO
 */
public record CryptoCategoryDto(
        Long id,
        String name,
        String description,
        Integer displayOrder,
        Boolean isActive,
        String color,
        Integer cryptocurrencyCount,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    
    public static CryptoCategoryDto of(String name, String description) {
        return new CryptoCategoryDto(
                null, name, description, 0, true, null, 0,
                null, null, null, null
        );
    }
    
    public static CryptoCategoryDto of(String name, String description, Integer displayOrder, String color) {
        return new CryptoCategoryDto(
                null, name, description, displayOrder, true, color, 0,
                null, null, null, null
        );
    }
    
    public static CryptoCategoryDto from(CryptoCategory entity) {
        return new CryptoCategoryDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getDisplayOrder(),
                entity.getIsActive(),
                entity.getColor(),
                entity.getCryptocurrencyCount(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }
    
    public CryptoCategory toEntity() {
        var category = CryptoCategory.of(name, description, displayOrder);
        if (color != null) {
            category.setColor(color);
        }
        if (isActive != null) {
            category.changeActiveStatus(isActive);
        }
        return category;
    }
}