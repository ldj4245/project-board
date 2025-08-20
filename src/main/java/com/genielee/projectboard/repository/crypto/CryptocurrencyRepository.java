package com.genielee.projectboard.repository.crypto;

import com.genielee.projectboard.domain.crypto.Cryptocurrency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 암호화폐 엔티티에 대한 데이터 액세스 리포지토리
 */
@Repository
public interface CryptocurrencyRepository extends JpaRepository<Cryptocurrency, String> {
    
    /**
     * 심볼로 암호화폐를 조회합니다
     */
    Optional<Cryptocurrency> findBySymbol(String symbol);
    
    /**
     * 활성 상태인 암호화폐 목록을 조회합니다
     */
    List<Cryptocurrency> findByIsActiveTrueOrderByRankAsc();
    
    /**
     * 활성 상태인 암호화폐 목록을 페이징하여 조회합니다
     */
    Page<Cryptocurrency> findByIsActiveTrueOrderByRankAsc(Pageable pageable);
    
    /**
     * 순위 범위 내의 암호화폐 목록을 조회합니다
     */
    List<Cryptocurrency> findByIsActiveTrueAndRankBetweenOrderByRankAsc(Integer startRank, Integer endRank);
    
    /**
     * 상위 N개 암호화폐 목록을 조회합니다
     */
    List<Cryptocurrency> findTop10ByIsActiveTrueOrderByRankAsc();
    
    /**
     * 특정 심볼들에 해당하는 암호화폐 목록을 조회합니다
     */
    List<Cryptocurrency> findBySymbolInAndIsActiveTrue(Set<String> symbols);
    
    /**
     * 이름 또는 심볼로 검색합니다
     */
    @Query("SELECT c FROM Cryptocurrency c WHERE c.isActive = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.symbol) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Cryptocurrency> searchByNameOrSymbol(@Param("keyword") String keyword);
    
    /**
     * 페이징된 검색 결과를 반환합니다
     */
    @Query("SELECT c FROM Cryptocurrency c WHERE c.isActive = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.symbol) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY c.rank ASC")
    Page<Cryptocurrency> searchByNameOrSymbol(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 카테고리별 암호화폐 개수를 조회합니다
     */
    @Query("SELECT COUNT(c) FROM Cryptocurrency c JOIN c.categories cat WHERE cat.id = :categoryId AND c.isActive = true")
    long countByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 특정 카테고리에 속한 암호화폐 목록을 조회합니다
     */
    @Query("SELECT c FROM Cryptocurrency c JOIN c.categories cat WHERE cat.id = :categoryId AND c.isActive = true ORDER BY c.rank ASC")
    List<Cryptocurrency> findByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 특정 카테고리에 속한 암호화폐 목록을 페이징하여 조회합니다
     */
    @Query("SELECT c FROM Cryptocurrency c JOIN c.categories cat WHERE cat.id = :categoryId AND c.isActive = true ORDER BY c.rank ASC")
    Page<Cryptocurrency> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    /**
     * 활성 암호화폐의 총 개수를 조회합니다
     */
    long countByIsActiveTrue();
}