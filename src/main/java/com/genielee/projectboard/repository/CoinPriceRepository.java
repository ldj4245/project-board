package com.genielee.projectboard.repository;

import com.genielee.projectboard.domain.Coin;
import com.genielee.projectboard.domain.CoinPrice;
import com.genielee.projectboard.domain.Exchange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 코인 시세 리포지토리
 * 실시간 시세 데이터 관리
 */
@RepositoryRestResource
public interface CoinPriceRepository extends JpaRepository<CoinPrice, Long> {

    // 특정 코인의 특정 거래소 최신 시세
    @Query("SELECT cp FROM CoinPrice cp WHERE cp.coin = :coin AND cp.exchange = :exchange AND cp.quoteCurrency = :quoteCurrency AND cp.isLatest = true")
    Optional<CoinPrice> findLatestPrice(@Param("coin") Coin coin, 
                                       @Param("exchange") Exchange exchange, 
                                       @Param("quoteCurrency") String quoteCurrency);

    // 특정 코인의 모든 거래소 최신 시세
    @Query("SELECT cp FROM CoinPrice cp WHERE cp.coin = :coin AND cp.isLatest = true ORDER BY cp.exchange.sortOrder")
    List<CoinPrice> findAllLatestPricesByCoin(@Param("coin") Coin coin);

    // 특정 거래소의 모든 활성 코인 최신 시세
    @Query("SELECT cp FROM CoinPrice cp WHERE cp.exchange = :exchange AND cp.isLatest = true AND cp.coin.isActive = true ORDER BY cp.coin.rank")
    List<CoinPrice> findAllLatestPricesByExchange(@Param("exchange") Exchange exchange);

    // 시가총액 상위 N개 코인의 최신 시세
    @Query("SELECT cp FROM CoinPrice cp WHERE cp.isLatest = true AND cp.coin.isActive = true AND cp.quoteCurrency = 'KRW' GROUP BY cp.coin ORDER BY cp.coin.rank")
    List<CoinPrice> findTopCoinPrices(Pageable pageable);

    // 특정 시간 이후의 시세 데이터
    List<CoinPrice> findByCoinAndExchangeAndPriceTimestampAfterOrderByPriceTimestampDesc(
            Coin coin, Exchange exchange, LocalDateTime since);

    // 김치프리미엄 계산용: 국내외 거래소 가격 비교
    @Query("SELECT cp FROM CoinPrice cp WHERE cp.coin.symbol = :symbol AND cp.exchange.code IN :exchangeCodes AND cp.isLatest = true")
    List<CoinPrice> findLatestPricesForKimchiPremium(@Param("symbol") String symbol, 
                                                    @Param("exchangeCodes") List<String> exchangeCodes);

    // 이전 시세를 최신이 아닌 것으로 업데이트
    @Modifying
    @Query("UPDATE CoinPrice cp SET cp.isLatest = false WHERE cp.coin = :coin AND cp.exchange = :exchange AND cp.quoteCurrency = :quoteCurrency AND cp.isLatest = true")
    void updatePreviousPricesToNotLatest(@Param("coin") Coin coin, 
                                        @Param("exchange") Exchange exchange, 
                                        @Param("quoteCurrency") String quoteCurrency);

    // 오래된 시세 데이터 정리 (7일 이전)
    @Modifying
    @Query("DELETE FROM CoinPrice cp WHERE cp.priceTimestamp < :cutoffTime AND cp.isLatest = false")
    void deleteOldPrices(@Param("cutoffTime") LocalDateTime cutoffTime);

    // 24시간 변동률 기준 상승/하락 코인 조회
    @Query("SELECT cp FROM CoinPrice cp WHERE cp.isLatest = true AND cp.changePercent24h > 0 ORDER BY cp.changePercent24h DESC")
    List<CoinPrice> findTopGainers(Pageable pageable);

    @Query("SELECT cp FROM CoinPrice cp WHERE cp.isLatest = true AND cp.changePercent24h < 0 ORDER BY cp.changePercent24h ASC")
    List<CoinPrice> findTopLosers(Pageable pageable);
}

