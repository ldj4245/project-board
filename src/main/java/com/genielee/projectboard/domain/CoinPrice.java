package com.genielee.projectboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 코인 시세 정보 엔티티
 * 실시간 시세 데이터 저장용
 */
@Getter
@ToString(callSuper = true)
@Table(
    indexes = {
        @Index(columnList = "coin_id, exchange_id"),
        @Index(columnList = "priceTimestamp"),
        @Index(columnList = "coin_id, priceTimestamp")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"coin_id", "exchange_id", "quoteCurrency", "priceTimestamp"})
    }
)
@Entity
public class CoinPrice extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coin_id")
    private Coin coin;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exchange_id")
    private Exchange exchange;

    @Setter
    @Column(nullable = false, length = 10)
    private String quoteCurrency = "KRW"; // KRW, USDT, BTC

    @Setter
    @Column(nullable = false, precision = 25, scale = 8)
    private BigDecimal price; // 현재 가격

    @Setter
    @Column(precision = 25, scale = 8)
    private BigDecimal previousPrice; // 이전 가격 (변동률 계산용)

    @Setter
    @Column(precision = 25, scale = 8)
    private BigDecimal volume24h; // 24시간 거래량

    @Setter
    @Column(precision = 10, scale = 4)
    private BigDecimal changePercent24h; // 24시간 변동률

    @Setter
    @Column(precision = 25, scale = 8)
    private BigDecimal high24h; // 24시간 최고가

    @Setter
    @Column(precision = 25, scale = 8)
    private BigDecimal low24h; // 24시간 최저가

    @Setter
    @Column(nullable = false)
    private LocalDateTime priceTimestamp; // 시세 시각

    @Setter
    @Column(nullable = false)
    private Boolean isLatest = true; // 최신 시세 여부

    protected CoinPrice() {}

    private CoinPrice(Coin coin, Exchange exchange, String quoteCurrency, BigDecimal price, LocalDateTime priceTimestamp) {
        this.coin = coin;
        this.exchange = exchange;
        this.quoteCurrency = quoteCurrency;
        this.price = price;
        this.priceTimestamp = priceTimestamp;
        this.isLatest = true;
    }

    public static CoinPrice of(Coin coin, Exchange exchange, String quoteCurrency, BigDecimal price, LocalDateTime priceTimestamp) {
        return new CoinPrice(coin, exchange, quoteCurrency, price, priceTimestamp);
    }

    public static CoinPrice of(Coin coin, Exchange exchange, BigDecimal price) {
        return new CoinPrice(coin, exchange, "KRW", price, LocalDateTime.now());
    }

    // 변동률 계산 메서드
    public BigDecimal calculateChangePercent() {
        if (previousPrice == null || previousPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return price.subtract(previousPrice)
                .divide(previousPrice, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    // 가격 상승/하락 여부
    public boolean isPriceUp() {
        if (previousPrice == null) return false;
        return price.compareTo(previousPrice) > 0;
    }

    public boolean isPriceDown() {
        if (previousPrice == null) return false;
        return price.compareTo(previousPrice) < 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof CoinPrice coinPrice)) return false;
        return Objects.equals(getId(), coinPrice.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}

