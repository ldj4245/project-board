package com.genielee.projectboard.repository;

import com.genielee.projectboard.domain.Coin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

/**
 * 코인 정보 리포지토리
 * 기존 Repository 패턴과 동일한 구조
 */
@RepositoryRestResource
public interface CoinRepository extends JpaRepository<Coin, Long> {

    // 심볼로 코인 검색
    Optional<Coin> findBySymbol(String symbol);

    // 심볼 목록으로 코인들 검색
    List<Coin> findBySymbolIn(List<String> symbols);

    // 활성 코인만 조회
    List<Coin> findByIsActiveTrueOrderByRankAsc();

    // 이름으로 코인 검색 (부분 일치)
    Page<Coin> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // 심볼로 코인 검색 (부분 일치)
    Page<Coin> findBySymbolContainingIgnoreCase(String symbol, Pageable pageable);

    // 시가총액 순위별 조회
    @Query("SELECT c FROM Coin c WHERE c.isActive = true ORDER BY c.rank ASC NULLS LAST")
    List<Coin> findTopCoinsByMarketCap(Pageable pageable);

    // 주요 코인들 조회 (상위 10개)
    @Query("SELECT c FROM Coin c WHERE c.isActive = true AND c.rank <= 10 ORDER BY c.rank ASC")
    List<Coin> findMajorCoins();

    // 심볼 존재 여부 확인
    boolean existsBySymbol(String symbol);

    // 활성 코인 개수
    long countByIsActiveTrue();
}

