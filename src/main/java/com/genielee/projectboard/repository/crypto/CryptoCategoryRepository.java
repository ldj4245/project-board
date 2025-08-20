package com.genielee.projectboard.repository.crypto;

import com.genielee.projectboard.domain.crypto.CryptoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 암호화폐 카테고리에 대한 데이터 액세스 리포지토리
 */
@Repository
public interface CryptoCategoryRepository extends JpaRepository<CryptoCategory, Long> {
    
    /**
     * 활성 상태인 카테고리 목록을 표시 순서대로 조회합니다
     */
    List<CryptoCategory> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    /**
     * 모든 카테고리를 표시 순서대로 조회합니다
     */
    List<CryptoCategory> findAllByOrderByDisplayOrderAsc();
    
    /**
     * 카테고리 이름으로 조회합니다
     */
    Optional<CryptoCategory> findByName(String name);
    
    /**
     * 카테고리 이름의 중복 여부를 확인합니다
     */
    boolean existsByName(String name);
    
    /**
     * 특정 ID를 제외하고 이름의 중복 여부를 확인합니다 (수정 시 사용)
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * 활성 카테고리의 개수를 조회합니다
     */
    long countByIsActiveTrue();
    
    /**
     * 카테고리 이름으로 검색합니다
     */
    @Query("SELECT c FROM CryptoCategory c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY c.displayOrder ASC")
    List<CryptoCategory> searchByNameOrDescription(@Param("keyword") String keyword);
    
    /**
     * 활성 상태인 카테고리 중에서 검색합니다
     */
    @Query("SELECT c FROM CryptoCategory c WHERE c.isActive = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY c.displayOrder ASC")
    List<CryptoCategory> searchActiveByNameOrDescription(@Param("keyword") String keyword);
    
    /**
     * 최대 표시 순서를 조회합니다
     */
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM CryptoCategory c")
    Integer findMaxDisplayOrder();
}