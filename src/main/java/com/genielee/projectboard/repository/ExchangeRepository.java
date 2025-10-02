package com.genielee.projectboard.repository;

import com.genielee.projectboard.domain.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

/**
 * 거래소 정보 리포지토리
 */
@RepositoryRestResource
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    // 코드로 거래소 검색
    Optional<Exchange> findByCode(String code);

    // 활성 거래소만 조회
    List<Exchange> findByIsActiveTrueOrderBySortOrderAsc();

    // 국내 거래소만 조회
    List<Exchange> findByIsDomesticTrueAndIsActiveTrueOrderBySortOrderAsc();

    // 해외 거래소만 조회
    List<Exchange> findByIsDomesticFalseAndIsActiveTrueOrderBySortOrderAsc();

    // 국가별 거래소 조회
    List<Exchange> findByCountryAndIsActiveTrueOrderBySortOrderAsc(String country);

    // 코드 존재 여부 확인
    boolean existsByCode(String code);
}

