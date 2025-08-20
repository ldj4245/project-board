package com.genielee.projectboard.domain.crypto;

import com.genielee.projectboard.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 암호화폐 가격 이력을 저장하는 도메인 엔티티
 * 차트 데이터 및 과거 가격 추적용
 */
@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "cryptocurrencyId"),
        @Index(columnList = "recordedAt"),
        @Index(columnList = "cryptocurrencyId, recordedAt")
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CryptoPriceHistory extends AuditingFields {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cryptocurrency_id")
    private Cryptocurrency cryptocurrency;
    
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal price; // 기록된 가격 (USD)
    
    @Column(precision = 20, scale = 8)
    private BigDecimal priceKrw; // 기록된 가격 (KRW)
    
    @Column(precision = 20, scale = 2)
    private BigDecimal volume; // 거래량
    
    @Column(precision = 15, scale = 2)
    private BigDecimal marketCap; // 시가총액
    
    @Column(nullable = false)
    private LocalDateTime recordedAt; // 기록 시간
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeFrame timeFrame; // 시간 프레임 (1분, 5분, 1시간, 1일 등)
    
    // 정적 팩토리 메서드
    public static CryptoPriceHistory of(Cryptocurrency cryptocurrency, BigDecimal price, 
                                       BigDecimal priceKrw, BigDecimal volume, 
                                       BigDecimal marketCap, TimeFrame timeFrame) {
        var priceHistory = new CryptoPriceHistory();
        priceHistory.cryptocurrency = cryptocurrency;
        priceHistory.price = price;
        priceHistory.priceKrw = priceKrw;
        priceHistory.volume = volume;
        priceHistory.marketCap = marketCap;
        priceHistory.timeFrame = timeFrame;
        priceHistory.recordedAt = LocalDateTime.now();
        return priceHistory;
    }
    
    public static CryptoPriceHistory of(Cryptocurrency cryptocurrency, BigDecimal price, 
                                       BigDecimal priceKrw, TimeFrame timeFrame) {
        return of(cryptocurrency, price, priceKrw, null, null, timeFrame);
    }
    
    /**
     * 시간 프레임 열거형
     */
    public enum TimeFrame {
        ONE_MINUTE("1m", "1분"),
        FIVE_MINUTES("5m", "5분"),
        FIFTEEN_MINUTES("15m", "15분"),
        THIRTY_MINUTES("30m", "30분"),
        ONE_HOUR("1h", "1시간"),
        FOUR_HOURS("4h", "4시간"),
        TWELVE_HOURS("12h", "12시간"),
        ONE_DAY("1d", "1일"),
        ONE_WEEK("1w", "1주"),
        ONE_MONTH("1M", "1개월");
        
        private final String code;
        private final String description;
        
        TimeFrame(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CryptoPriceHistory that)) return false;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}