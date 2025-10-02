package com.genielee.projectboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * 거래소 정보 엔티티
 */
@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "code", unique = true),
        @Index(columnList = "country"),
        @Index(columnList = "isActive")
})
@Entity
public class Exchange extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 20, unique = true)
    private String code; // upbit, binance, bithumb

    @Setter
    @Column(nullable = false, length = 100)
    private String name; // 업비트, 바이낸스, 빗썸

    @Setter
    @Column(nullable = false, length = 100)
    private String nameEn; // Upbit, Binance, Bithumb

    @Setter
    @Column(nullable = false, length = 10)
    private String country; // KR, US, CN

    @Setter
    @Column(length = 255)
    private String apiBaseUrl; // API 기본 URL

    @Setter
    @Column(length = 255)
    private String websiteUrl; // 거래소 웹사이트

    @Setter
    @Column(length = 255)
    private String logoUrl; // 거래소 로고

    @Setter
    @Column(nullable = false)
    private Boolean isActive = true; // 활성 상태

    @Setter
    @Column(nullable = false)
    private Boolean isDomestic; // 국내 거래소 여부

    @Setter
    private Integer sortOrder; // 표시 순서

    protected Exchange() {}

    private Exchange(String code, String name, String nameEn, String country, Boolean isDomestic) {
        this.code = code.toLowerCase();
        this.name = name;
        this.nameEn = nameEn;
        this.country = country.toUpperCase();
        this.isDomestic = isDomestic;
        this.isActive = true;
    }

    public static Exchange of(String code, String name, String nameEn, String country, Boolean isDomestic) {
        return new Exchange(code, name, nameEn, country, isDomestic);
    }

    // 국내 거래소 생성 헬퍼 메서드
    public static Exchange ofDomestic(String code, String name, String nameEn) {
        return new Exchange(code, name, nameEn, "KR", true);
    }

    // 해외 거래소 생성 헬퍼 메서드
    public static Exchange ofInternational(String code, String name, String nameEn, String country) {
        return new Exchange(code, name, nameEn, country, false);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Exchange exchange)) return false;
        return Objects.equals(getId(), exchange.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}

