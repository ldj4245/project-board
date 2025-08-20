package com.genielee.projectboard.service.crypto;

import com.genielee.projectboard.domain.crypto.CryptoCategory;
import com.genielee.projectboard.domain.crypto.Cryptocurrency;
import com.genielee.projectboard.dto.crypto.CryptoCategoryDto;
import com.genielee.projectboard.dto.crypto.CryptocurrencyDto;
import com.genielee.projectboard.repository.crypto.CryptoCategoryRepository;
import com.genielee.projectboard.repository.crypto.CryptocurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 암호화폐 카테고리 관리 서비스
 * 카테고리별 암호화폐 분류 및 관리 기능 제공
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CryptoCategoryService {
    
    private final CryptoCategoryRepository cryptoCategoryRepository;
    private final CryptocurrencyRepository cryptocurrencyRepository;
    
    /**
     * 새로운 카테고리를 생성합니다
     */
    public CryptoCategoryDto createCategory(CryptoCategoryDto categoryDto) {
        log.info("새 암호화폐 카테고리 생성: {}", categoryDto.name());
        
        // 카테고리 이름 중복 검사
        if (cryptoCategoryRepository.existsByName(categoryDto.name())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + categoryDto.name());
        }
        
        // 표시 순서가 없으면 자동 설정
        Integer displayOrder = categoryDto.displayOrder();
        if (displayOrder == null || displayOrder == 0) {
            displayOrder = cryptoCategoryRepository.findMaxDisplayOrder() + 1;
        }
        
        var category = CryptoCategory.of(categoryDto.name(), categoryDto.description(), displayOrder);
        if (categoryDto.color() != null) {
            category.setColor(categoryDto.color());
        }
        
        var savedCategory = cryptoCategoryRepository.save(category);
        log.info("암호화폐 카테고리 생성 완료: {} (ID: {})", savedCategory.getName(), savedCategory.getId());
        
        return CryptoCategoryDto.from(savedCategory);
    }
    
    /**
     * 카테고리 정보를 수정합니다
     */
    public CryptoCategoryDto updateCategory(Long categoryId, CryptoCategoryDto categoryDto) {
        log.info("암호화폐 카테고리 수정: ID={}, 이름={}", categoryId, categoryDto.name());
        
        var category = cryptoCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryId));
        
        // 카테고리 이름 중복 검사 (자신 제외)
        if (cryptoCategoryRepository.existsByNameAndIdNot(categoryDto.name(), categoryId)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + categoryDto.name());
        }
        
        category.updateInfo(categoryDto.name(), categoryDto.description(), categoryDto.color());
        
        if (categoryDto.displayOrder() != null) {
            category.changeDisplayOrder(categoryDto.displayOrder());
        }
        
        if (categoryDto.isActive() != null) {
            category.changeActiveStatus(categoryDto.isActive());
        }
        
        var updatedCategory = cryptoCategoryRepository.save(category);
        log.info("암호화폐 카테고리 수정 완료: {}", updatedCategory.getName());
        
        return CryptoCategoryDto.from(updatedCategory);
    }
    
    /**
     * 카테고리를 삭제합니다
     */
    public void deleteCategory(Long categoryId) {
        log.info("암호화폐 카테고리 삭제: ID={}", categoryId);
        
        var category = cryptoCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryId));
        
        // 연관된 암호화폐들의 카테고리 관계 제거
        var cryptocurrencies = cryptocurrencyRepository.findByCategoryId(categoryId);
        cryptocurrencies.forEach(crypto -> crypto.removeCategory(category));
        
        cryptoCategoryRepository.delete(category);
        log.info("암호화폐 카테고리 삭제 완료: {}", category.getName());
    }
    
    /**
     * 활성 카테고리 목록을 조회합니다
     */
    @Transactional(readOnly = true)
    public List<CryptoCategoryDto> getActiveCategories() {
        log.debug("활성 암호화폐 카테고리 목록 조회");
        return cryptoCategoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(CryptoCategoryDto::from)
                .toList();
    }
    
    /**
     * 모든 카테고리 목록을 조회합니다
     */
    @Transactional(readOnly = true)
    public List<CryptoCategoryDto> getAllCategories() {
        log.debug("전체 암호화폐 카테고리 목록 조회");
        return cryptoCategoryRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(CryptoCategoryDto::from)
                .toList();
    }
    
    /**
     * ID로 카테고리를 조회합니다
     */
    @Transactional(readOnly = true)
    public Optional<CryptoCategoryDto> getCategoryById(Long categoryId) {
        log.debug("암호화폐 카테고리 조회: ID={}", categoryId);
        return cryptoCategoryRepository.findById(categoryId)
                .map(CryptoCategoryDto::from);
    }
    
    /**
     * 이름으로 카테고리를 조회합니다
     */
    @Transactional(readOnly = true)
    public Optional<CryptoCategoryDto> getCategoryByName(String name) {
        log.debug("암호화폐 카테고리 조회: 이름={}", name);
        return cryptoCategoryRepository.findByName(name)
                .map(CryptoCategoryDto::from);
    }
    
    /**
     * 카테고리명 또는 설명으로 검색합니다
     */
    @Transactional(readOnly = true)
    public List<CryptoCategoryDto> searchCategories(String keyword) {
        log.debug("암호화폐 카테고리 검색: {}", keyword);
        return cryptoCategoryRepository.searchByNameOrDescription(keyword)
                .stream()
                .map(CryptoCategoryDto::from)
                .toList();
    }
    
    /**
     * 특정 카테고리에 속한 암호화폐 목록을 조회합니다
     */
    @Transactional(readOnly = true)
    public List<CryptocurrencyDto> getCryptocurrenciesByCategory(Long categoryId) {
        log.debug("카테고리별 암호화폐 목록 조회: 카테고리 ID={}", categoryId);
        return cryptocurrencyRepository.findByCategoryId(categoryId)
                .stream()
                .map(CryptocurrencyDto::from)
                .toList();
    }
    
    /**
     * 특정 카테고리에 속한 암호화폐 목록을 페이징하여 조회합니다
     */
    @Transactional(readOnly = true)
    public Page<CryptocurrencyDto> getCryptocurrenciesByCategory(Long categoryId, Pageable pageable) {
        log.debug("카테고리별 암호화폐 페이징 목록 조회: 카테고리 ID={}, page={}", 
                categoryId, pageable.getPageNumber());
        return cryptocurrencyRepository.findByCategoryId(categoryId, pageable)
                .map(CryptocurrencyDto::from);
    }
    
    /**
     * 암호화폐를 카테고리에 추가합니다
     */
    public void addCryptocurrencyToCategory(String cryptocurrencyId, Long categoryId) {
        log.info("암호화폐를 카테고리에 추가: 암호화폐={}, 카테고리={}", cryptocurrencyId, categoryId);
        
        var cryptocurrency = cryptocurrencyRepository.findById(cryptocurrencyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 암호화폐입니다: " + cryptocurrencyId));
        
        var category = cryptoCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryId));
        
        cryptocurrency.addCategory(category);
        cryptocurrencyRepository.save(cryptocurrency);
        
        log.info("암호화폐를 카테고리에 추가 완료: {} -> {}", 
                cryptocurrency.getName(), category.getName());
    }
    
    /**
     * 암호화폐를 카테고리에서 제거합니다
     */
    public void removeCryptocurrencyFromCategory(String cryptocurrencyId, Long categoryId) {
        log.info("암호화폐를 카테고리에서 제거: 암호화폐={}, 카테고리={}", cryptocurrencyId, categoryId);
        
        var cryptocurrency = cryptocurrencyRepository.findById(cryptocurrencyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 암호화폐입니다: " + cryptocurrencyId));
        
        var category = cryptoCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryId));
        
        cryptocurrency.removeCategory(category);
        cryptocurrencyRepository.save(cryptocurrency);
        
        log.info("암호화폐를 카테고리에서 제거 완료: {} -> {}", 
                cryptocurrency.getName(), category.getName());
    }
    
    /**
     * 기본 카테고리들을 초기화합니다
     */
    public void initializeDefaultCategories() {
        log.info("기본 암호화폐 카테고리 초기화 시작");
        
        if (cryptoCategoryRepository.count() > 0) {
            log.info("이미 카테고리가 존재하여 초기화를 건너뜁니다");
            return;
        }
        
        var defaultCategories = List.of(
                CryptoCategoryDto.of("메이저코인", "비트코인, 이더리움 등 주요 암호화폐", 1, "#FF5722"),
                CryptoCategoryDto.of("알트코인", "비트코인을 제외한 대체 암호화폐", 2, "#2196F3"),
                CryptoCategoryDto.of("DeFi", "탈중앙화 금융 관련 암호화폐", 3, "#4CAF50"),
                CryptoCategoryDto.of("NFT", "NFT 관련 암호화폐", 4, "#9C27B0"),
                CryptoCategoryDto.of("게임", "게임 관련 암호화폐", 5, "#FF9800"),
                CryptoCategoryDto.of("메타버스", "메타버스 관련 암호화폐", 6, "#E91E63"),
                CryptoCategoryDto.of("밈코인", "밈 기반 암호화폐", 7, "#FFEB3B")
        );
        
        defaultCategories.forEach(this::createCategory);
        
        log.info("기본 암호화폐 카테고리 초기화 완료: {} 개", defaultCategories.size());
    }
}