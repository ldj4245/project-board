package com.genielee.projectboard.repository.crypto;

import com.genielee.projectboard.domain.crypto.CryptoPriceHistory;
import com.genielee.projectboard.domain.crypto.CryptoPriceHistory.TimeFrame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 암호화폐 가격 이력에 대한 데이터 액세스 리포지토리
 */
@Repository
public interface CryptoPriceHistoryRepository extends JpaRepository<CryptoPriceHistory, Long> {
    
    /**
     * 특정 암호화폐의 가격 이력을 시간순으로 조회합니다
     */
    List<CryptoPriceHistory> findByCryptocurrencyIdOrderByRecordedAtDesc(String cryptocurrencyId);
    
    /**
     * 특정 암호화폐의 특정 시간프레임 가격 이력을 조회합니다
     */
    List<CryptoPriceHistory> findByCryptocurrencyIdAndTimeFrameOrderByRecordedAtDesc(
            String cryptocurrencyId, TimeFrame timeFrame);
    
    /**
     * 특정 기간 내의 가격 이력을 조회합니다
     */
    List<CryptoPriceHistory> findByCryptocurrencyIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            String cryptocurrencyId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 특정 기간 내의 특정 시간프레임 가격 이력을 조회합니다
     */
    List<CryptoPriceHistory> findByCryptocurrencyIdAndTimeFrameAndRecordedAtBetweenOrderByRecordedAtAsc(
            String cryptocurrencyId, TimeFrame timeFrame, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 페이징된 가격 이력을 조회합니다
     */
    Page<CryptoPriceHistory> findByCryptocurrencyIdOrderByRecordedAtDesc(
            String cryptocurrencyId, Pageable pageable);
    
    /**
     * 특정 암호화폐의 최신 가격 기록을 조회합니다
     */
    @Query("SELECT h FROM CryptoPriceHistory h WHERE h.cryptocurrency.id = :cryptocurrencyId " +
           "ORDER BY h.recordedAt DESC LIMIT 1")
    CryptoPriceHistory findLatestByCryptocurrencyId(@Param("cryptocurrencyId") String cryptocurrencyId);
    
    /**
     * 특정 시간프레임의 최신 가격 기록을 조회합니다
     */
    @Query("SELECT h FROM CryptoPriceHistory h WHERE h.cryptocurrency.id = :cryptocurrencyId " +
           "AND h.timeFrame = :timeFrame ORDER BY h.recordedAt DESC LIMIT 1")
    CryptoPriceHistory findLatestByCryptocurrencyIdAndTimeFrame(
            @Param("cryptocurrencyId") String cryptocurrencyId, @Param("timeFrame") TimeFrame timeFrame);
    
    /**
     * 차트 데이터용 제한된 개수의 가격 이력을 조회합니다
     */
    @Query("SELECT h FROM CryptoPriceHistory h WHERE h.cryptocurrency.id = :cryptocurrencyId " +
           "AND h.timeFrame = :timeFrame ORDER BY h.recordedAt DESC LIMIT :limit")
    List<CryptoPriceHistory> findChartData(@Param("cryptocurrencyId") String cryptocurrencyId,
                                          @Param("timeFrame") TimeFrame timeFrame,
                                          @Param("limit") int limit);
    
    /**
     * 특정 시간 이전의 오래된 데이터를 조회합니다 (정리용)
     */
    List<CryptoPriceHistory> findByRecordedAtBeforeAndTimeFrame(LocalDateTime cutoffTime, TimeFrame timeFrame);
    
    /**
     * 특정 시간 이전의 오래된 데이터를 삭제합니다
     */
    void deleteByRecordedAtBeforeAndTimeFrame(LocalDateTime cutoffTime, TimeFrame timeFrame);
    
    /**
     * 특정 암호화폐의 가격 이력 개수를 조회합니다
     */
    long countByCryptocurrencyId(String cryptocurrencyId);
    
    /**
     * 특정 암호화폐의 특정 시간프레임 가격 이력 개수를 조회합니다
     */
    long countByCryptocurrencyIdAndTimeFrame(String cryptocurrencyId, TimeFrame timeFrame);
}