package com.genielee.projectboard.domain.crypto;

import com.genielee.projectboard.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 암호화폐 카테고리를 나타내는 도메인 엔티티
 * 게시판에서 코인별 카테고리 분류에 사용
 */
@Getter
@ToString(callSuper = true, exclude = "cryptocurrencies")
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "displayOrder"),
        @Index(columnList = "isActive")
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CryptoCategory extends AuditingFields {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String name; // 카테고리 이름 (예: "메이저코인", "알트코인", "DeFi", "NFT")
    
    @Column(length = 200)
    private String description; // 카테고리 설명
    
    @Column(nullable = false)
    private Integer displayOrder = 0; // 표시 순서
    
    @Column(nullable = false)
    private Boolean isActive = true; // 활성 상태
    
    @Column(length = 7)
    private String color; // 카테고리 색상 코드 (예: "#FF5722")
    
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<Cryptocurrency> cryptocurrencies = new LinkedHashSet<>();
    
    // 정적 팩토리 메서드
    public static CryptoCategory of(String name, String description) {
        var category = new CryptoCategory();
        category.name = name;
        category.description = description;
        category.isActive = true;
        category.displayOrder = 0;
        return category;
    }
    
    public static CryptoCategory of(String name, String description, Integer displayOrder) {
        var category = of(name, description);
        category.displayOrder = displayOrder;
        return category;
    }
    
    // 비즈니스 메서드
    /**
     * 카테고리 정보를 업데이트합니다
     */
    public void updateInfo(String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }
    
    /**
     * 표시 순서를 변경합니다
     */
    public void changeDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    /**
     * 활성 상태를 변경합니다
     */
    public void changeActiveStatus(boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * 카테고리 색상을 설정합니다
     */
    public void setColor(String color) {
        // 색상 코드 유효성 검증 (간단한 형태)
        if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
            this.color = color;
        }
    }
    
    /**
     * 암호화폐를 카테고리에 추가합니다
     */
    public void addCryptocurrency(Cryptocurrency cryptocurrency) {
        this.cryptocurrencies.add(cryptocurrency);
    }
    
    /**
     * 암호화폐를 카테고리에서 제거합니다
     */
    public void removeCryptocurrency(Cryptocurrency cryptocurrency) {
        this.cryptocurrencies.remove(cryptocurrency);
    }
    
    /**
     * 카테고리에 포함된 암호화폐 개수를 반환합니다
     */
    public int getCryptocurrencyCount() {
        return cryptocurrencies.size();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CryptoCategory that)) return false;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}