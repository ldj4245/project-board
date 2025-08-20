package com.genielee.projectboard.domain.crypto;

import com.genielee.projectboard.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 암호화폐 정보를 나타내는 도메인 엔티티
 * 코인의 기본 정보와 현재 가격 정보를 포함
 */
@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "symbol"),
        @Index(columnList = "name"),
        @Index(columnList = "rank"),
        @Index(columnList = "createdAt")
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cryptocurrency extends AuditingFields {
    
    @Id
    private String id; // 코인게코 API의 id (예: "bitcoin", "ethereum")
    
    @Column(nullable = false, unique = true, length = 10)
    private String symbol; // 심볼 (예: "BTC", "ETH")
    
    @Column(nullable = false, length = 100)
    private String name; // 이름 (예: "Bitcoin", "Ethereum")
    
    @Column(length = 500)
    private String image; // 아이콘 이미지 URL
    
    @Column(precision = 20, scale = 8)
    private BigDecimal currentPrice; // 현재 가격 (USD)
    
    @Column(precision = 20, scale = 8)
    private BigDecimal currentPriceKrw; // 현재 가격 (KRW)
    
    @Column(precision = 15, scale = 2)
    private BigDecimal marketCap; // 시가총액
    
    @Column(name = "market_cap_rank")
    private Integer rank; // 시가총액 순위
    
    @Column(precision = 5, scale = 2)
    private BigDecimal priceChangePercentage24h; // 24시간 가격 변동률
    
    @Column(precision = 20, scale = 8)
    private BigDecimal priceChange24h; // 24시간 가격 변동량
    
    @Column(precision = 20, scale = 8)
    private BigDecimal high24h; // 24시간 최고가
    
    @Column(precision = 20, scale = 8)
    private BigDecimal low24h; // 24시간 최저가
    
    @Column(precision = 20, scale = 2)
    private BigDecimal totalVolume; // 24시간 거래량
    
    @Column(nullable = false)
    private Boolean isActive = true; // 활성 상태
    
    @ToString.Exclude
    @JoinTable(
            name = "cryptocurrency_category",
            joinColumns = @JoinColumn(name = "cryptocurrency_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<CryptoCategory> categories = new LinkedHashSet<>();
    
    // 정적 팩토리 메서드
    public static Cryptocurrency of(String id, String symbol, String name) {
        var cryptocurrency = new Cryptocurrency();
        cryptocurrency.id = id;
        cryptocurrency.symbol = symbol.toUpperCase();
        cryptocurrency.name = name;
        cryptocurrency.isActive = true;
        return cryptocurrency;
    }
    
    // 비즈니스 메서드
    /**
     * 코인 가격 정보를 업데이트합니다
     */
    public void updatePriceInfo(BigDecimal currentPrice, BigDecimal currentPriceKrw, 
                               BigDecimal marketCap, Integer rank,
                               BigDecimal priceChangePercentage24h, BigDecimal priceChange24h,
                               BigDecimal high24h, BigDecimal low24h, BigDecimal totalVolume) {
        this.currentPrice = currentPrice;
        this.currentPriceKrw = currentPriceKrw;
        this.marketCap = marketCap;
        this.rank = rank;
        this.priceChangePercentage24h = priceChangePercentage24h;
        this.priceChange24h = priceChange24h;
        this.high24h = high24h;
        this.low24h = low24h;
        this.totalVolume = totalVolume;
    }
    
    /**
     * 코인 이미지 URL을 업데이트합니다
     */
    public void updateImage(String imageUrl) {
        this.image = imageUrl;
    }
    
    /**
     * 코인 활성 상태를 변경합니다
     */
    public void changeActiveStatus(boolean isActive) {
        this.isActive = isActive;
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
    
    /**
     * 카테고리를 추가합니다
     */
    public void addCategory(CryptoCategory category) {
        this.categories.add(category);
        category.addCryptocurrency(this);
    }
    
    /**
     * 카테고리를 제거합니다
     */
    public void removeCategory(CryptoCategory category) {
        this.categories.remove(category);
        category.removeCryptocurrency(this);
    }
    
    /**
     * 모든 카테고리를 제거합니다
     */
    public void clearCategories() {
        this.categories.forEach(category -> category.removeCryptocurrency(this));
        this.categories.clear();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cryptocurrency that)) return false;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}