package com.genielee.projectboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 코인 기본 정보 엔티티
 * 기존 Article-Hashtag 패턴과 동일한 구조로 설계
 */
@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "symbol", unique = true),
        @Index(columnList = "name"),
        @Index(columnList = "marketCap"),
        @Index(columnList = "isActive")
})
@Entity
public class Coin extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 20, unique = true)
    private String symbol; // BTC, ETH, ADA

    @Setter
    @Column(nullable = false, length = 100)
    private String name; // Bitcoin, Ethereum, Cardano

    @Setter
    @Column(length = 500)
    private String description; // 코인 설명

    @Setter
    @Column(length = 255)
    private String logoUrl; // 코인 로고 이미지 URL

    @Setter
    @Column(length = 255)
    private String websiteUrl; // 공식 웹사이트

    @Setter
    @Column(precision = 25, scale = 8)
    private BigDecimal marketCap; // 시가총액

    @Setter
    @Column(precision = 10, scale = 2)
    private BigDecimal circulatingSupply; // 순환 공급량

    @Setter
    @Column(precision = 10, scale = 2)
    private BigDecimal totalSupply; // 총 공급량

    @Setter
    @Column(nullable = false)
    private Boolean isActive = true; // 활성 상태

    @Setter
    private Integer rank; // 시가총액 순위

    protected Coin() {}

    private Coin(String symbol, String name, String description) {
        this.symbol = symbol.toUpperCase();
        this.name = name;
        this.description = description;
        this.isActive = true;
    }

    public static Coin of(String symbol, String name, String description) {
        return new Coin(symbol, name, description);
    }

    public static Coin of(String symbol, String name) {
        return new Coin(symbol, name, null);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Coin coin)) return false;
        return Objects.equals(getId(), coin.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}

