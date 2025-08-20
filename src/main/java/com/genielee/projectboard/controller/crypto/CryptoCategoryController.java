package com.genielee.projectboard.controller.crypto;

import com.genielee.projectboard.dto.crypto.CryptoCategoryDto;
import com.genielee.projectboard.dto.crypto.CryptocurrencyDto;
import com.genielee.projectboard.service.crypto.CryptoCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 암호화폐 카테고리 관리 REST API 컨트롤러
 */
@Tag(name = "암호화폐 카테고리 API", description = "암호화폐 카테고리 관리 및 조회")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/crypto/categories")
public class CryptoCategoryController {
    
    private final CryptoCategoryService cryptoCategoryService;
    
    /**
     * 활성 카테고리 목록을 조회합니다
     */
    @Operation(summary = "활성 카테고리 목록 조회", description = "활성 상태인 암호화폐 카테고리 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<List<CryptoCategoryDto>> getActiveCategories() {
        log.debug("REST API: 활성 암호화폐 카테고리 목록 조회");
        var categories = cryptoCategoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * 모든 카테고리 목록을 조회합니다 (관리자용)
     */
    @Operation(summary = "전체 카테고리 목록 조회", description = "모든 암호화폐 카테고리 목록을 조회합니다 (관리자용)")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CryptoCategoryDto>> getAllCategories() {
        log.debug("REST API: 전체 암호화폐 카테고리 목록 조회");
        var categories = cryptoCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * ID로 특정 카테고리를 조회합니다
     */
    @Operation(summary = "카테고리 상세 조회", description = "ID로 특정 카테고리의 상세 정보를 조회합니다")
    @GetMapping("/{id}")
    public ResponseEntity<CryptoCategoryDto> getCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long id) {
        log.debug("REST API: 암호화폐 카테고리 상세 조회 (ID={})", id);
        return cryptoCategoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 특정 카테고리에 속한 암호화폐 목록을 조회합니다
     */
    @Operation(summary = "카테고리별 암호화폐 목록 조회", description = "특정 카테고리에 속한 암호화폐 목록을 조회합니다")
    @GetMapping("/{id}/cryptocurrencies")
    public ResponseEntity<Page<CryptocurrencyDto>> getCryptocurrenciesByCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "페이징 정보")
            @PageableDefault(size = 20, sort = "rank", direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST API: 카테고리별 암호화폐 목록 조회 (카테고리 ID={}, page={})", id, pageable.getPageNumber());
        var cryptocurrencies = cryptoCategoryService.getCryptocurrenciesByCategory(id, pageable);
        return ResponseEntity.ok(cryptocurrencies);
    }
    
    /**
     * 카테고리명 또는 설명으로 검색합니다
     */
    @Operation(summary = "카테고리 검색", description = "이름 또는 설명으로 카테고리를 검색합니다")
    @GetMapping("/search")
    public ResponseEntity<List<CryptoCategoryDto>> searchCategories(
            @Parameter(description = "검색 키워드", example = "메이저")
            @RequestParam String keyword) {
        log.debug("REST API: 암호화폐 카테고리 검색 (키워드={})", keyword);
        var categories = cryptoCategoryService.searchCategories(keyword);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * 새로운 카테고리를 생성합니다 (관리자용)
     */
    @Operation(summary = "카테고리 생성", description = "새로운 암호화폐 카테고리를 생성합니다 (관리자용)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CryptoCategoryDto> createCategory(
            @Parameter(description = "생성할 카테고리 정보")
            @Valid @RequestBody CryptoCategoryDto categoryDto) {
        log.info("REST API: 암호화폐 카테고리 생성 요청 - {}", categoryDto.name());
        try {
            var createdCategory = cryptoCategoryService.createCategory(categoryDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (IllegalArgumentException e) {
            log.warn("카테고리 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 카테고리 정보를 수정합니다 (관리자용)
     */
    @Operation(summary = "카테고리 수정", description = "카테고리 정보를 수정합니다 (관리자용)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CryptoCategoryDto> updateCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "수정할 카테고리 정보")
            @Valid @RequestBody CryptoCategoryDto categoryDto) {
        log.info("REST API: 암호화폐 카테고리 수정 요청 - ID={}, 이름={}", id, categoryDto.name());
        try {
            var updatedCategory = cryptoCategoryService.updateCategory(id, categoryDto);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            log.warn("카테고리 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 카테고리를 삭제합니다 (관리자용)
     */
    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다 (관리자용)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long id) {
        log.info("REST API: 암호화폐 카테고리 삭제 요청 - ID={}", id);
        try {
            cryptoCategoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("카테고리 삭제 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 암호화폐를 카테고리에 추가합니다 (관리자용)
     */
    @Operation(summary = "암호화폐 카테고리 추가", description = "암호화폐를 특정 카테고리에 추가합니다 (관리자용)")
    @PostMapping("/{categoryId}/cryptocurrencies/{cryptocurrencyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addCryptocurrencyToCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long categoryId,
            @Parameter(description = "암호화폐 ID", example = "bitcoin")
            @PathVariable String cryptocurrencyId) {
        log.info("REST API: 암호화폐를 카테고리에 추가 - 암호화폐={}, 카테고리={}", cryptocurrencyId, categoryId);
        try {
            cryptoCategoryService.addCryptocurrencyToCategory(cryptocurrencyId, categoryId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("암호화폐 카테고리 추가 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 암호화폐를 카테고리에서 제거합니다 (관리자용)
     */
    @Operation(summary = "암호화폐 카테고리 제거", description = "암호화폐를 특정 카테고리에서 제거합니다 (관리자용)")
    @DeleteMapping("/{categoryId}/cryptocurrencies/{cryptocurrencyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeCryptocurrencyFromCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long categoryId,
            @Parameter(description = "암호화폐 ID", example = "bitcoin")
            @PathVariable String cryptocurrencyId) {
        log.info("REST API: 암호화폐를 카테고리에서 제거 - 암호화폐={}, 카테고리={}", cryptocurrencyId, categoryId);
        try {
            cryptoCategoryService.removeCryptocurrencyFromCategory(cryptocurrencyId, categoryId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("암호화폐 카테고리 제거 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}