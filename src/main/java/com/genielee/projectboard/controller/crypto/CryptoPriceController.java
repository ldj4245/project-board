package com.genielee.projectboard.controller.crypto;

import com.genielee.projectboard.dto.crypto.CryptocurrencyDto;
import com.genielee.projectboard.service.crypto.CryptoPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 암호화폐 가격 정보 REST API 컨트롤러
 */
@Tag(name = "암호화폐 가격 API", description = "암호화폐 가격 정보 조회 및 관리")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/crypto/prices")
public class CryptoPriceController {
    
    private final CryptoPriceService cryptoPriceService;
    
    /**
     * 상위 암호화폐 목록을 조회합니다
     */
    @Operation(summary = "상위 암호화폐 목록 조회", description = "시가총액 기준 상위 10개 암호화폐 목록을 조회합니다")
    @GetMapping("/top")
    public ResponseEntity<List<CryptocurrencyDto>> getTopCryptocurrencies() {
        log.debug("REST API: 상위 암호화폐 목록 조회");
        var cryptocurrencies = cryptoPriceService.getTopCryptocurrencies();
        return ResponseEntity.ok(cryptocurrencies);
    }
    
    /**
     * 활성 암호화폐 목록을 페이징하여 조회합니다
     */
    @Operation(summary = "암호화폐 목록 조회", description = "활성 상태인 암호화폐 목록을 페이징하여 조회합니다")
    @GetMapping
    public ResponseEntity<Page<CryptocurrencyDto>> getCryptocurrencies(
            @Parameter(description = "페이징 정보")
            @PageableDefault(size = 20, sort = "rank", direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST API: 암호화폐 목록 조회 (page={})", pageable.getPageNumber());
        var cryptocurrencies = cryptoPriceService.getActiveCryptocurrencies(pageable);
        return ResponseEntity.ok(cryptocurrencies);
    }
    
    /**
     * ID로 특정 암호화폐 정보를 조회합니다
     */
    @Operation(summary = "암호화폐 상세 조회", description = "ID로 특정 암호화폐의 상세 정보를 조회합니다")
    @GetMapping("/{id}")
    public ResponseEntity<CryptocurrencyDto> getCryptocurrency(
            @Parameter(description = "암호화폐 ID", example = "bitcoin")
            @PathVariable String id) {
        log.debug("REST API: 암호화폐 상세 조회 (ID={})", id);
        return cryptoPriceService.getCryptocurrencyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 심볼로 특정 암호화폐 정보를 조회합니다
     */
    @Operation(summary = "심볼로 암호화폐 조회", description = "심볼로 특정 암호화폐의 정보를 조회합니다")
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<CryptocurrencyDto> getCryptocurrencyBySymbol(
            @Parameter(description = "암호화폐 심볼", example = "BTC")
            @PathVariable String symbol) {
        log.debug("REST API: 심볼로 암호화폐 조회 (심볼={})", symbol);
        return cryptoPriceService.getCryptocurrencyBySymbol(symbol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 암호화폐명 또는 심볼로 검색합니다
     */
    @Operation(summary = "암호화폐 검색", description = "이름 또는 심볼로 암호화폐를 검색합니다")
    @GetMapping("/search")
    public ResponseEntity<Page<CryptocurrencyDto>> searchCryptocurrencies(
            @Parameter(description = "검색 키워드", example = "bitcoin")
            @RequestParam String keyword,
            @Parameter(description = "페이징 정보")
            @PageableDefault(size = 20, sort = "rank", direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST API: 암호화폐 검색 (키워드={}, page={})", keyword, pageable.getPageNumber());
        var cryptocurrencies = cryptoPriceService.searchCryptocurrencies(keyword, pageable);
        return ResponseEntity.ok(cryptocurrencies);
    }
    
    /**
     * 특정 암호화폐들의 가격 정보를 조회합니다
     */
    @Operation(summary = "특정 암호화폐 가격 조회", description = "여러 암호화폐의 현재 가격을 조회합니다")
    @PostMapping("/batch")
    public Mono<ResponseEntity<List<CryptocurrencyDto>>> getCryptocurrencyPrices(
            @Parameter(description = "조회할 암호화폐 ID 목록")
            @RequestBody List<String> coinIds) {
        log.debug("REST API: 특정 암호화폐 가격 조회 (개수={})", coinIds.size());
        return cryptoPriceService.getCryptocurrencyPrices(coinIds)
                .map(ResponseEntity::ok);
    }
    
    /**
     * 암호화폐 가격 정보를 수동으로 업데이트합니다
     */
    @Operation(summary = "가격 정보 수동 업데이트", description = "상위 암호화폐 가격 정보를 수동으로 업데이트합니다")
    @PostMapping("/update")
    public Mono<ResponseEntity<List<CryptocurrencyDto>>> updateCryptocurrencyPrices() {
        log.info("REST API: 암호화폐 가격 정보 수동 업데이트 요청");
        return cryptoPriceService.updateTopCryptocurrencyPrices()
                .map(ResponseEntity::ok);
    }
    
    /**
     * API 연결 상태를 확인합니다
     */
    @Operation(summary = "API 연결 상태 확인", description = "외부 API 연결 상태를 확인합니다")
    @GetMapping("/health")
    public Mono<ResponseEntity<Boolean>> checkApiHealth() {
        log.debug("REST API: API 연결 상태 확인");
        return cryptoPriceService.checkApiConnection()
                .map(ResponseEntity::ok);
    }
}