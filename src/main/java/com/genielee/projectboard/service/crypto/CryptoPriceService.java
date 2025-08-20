package com.genielee.projectboard.service.crypto;

import com.genielee.projectboard.domain.crypto.Cryptocurrency;
import com.genielee.projectboard.dto.crypto.CryptocurrencyDto;
import com.genielee.projectboard.infrastructure.crypto.CoinGeckoApiClient;
import com.genielee.projectboard.infrastructure.crypto.CoinGeckoMarketData;
import com.genielee.projectboard.infrastructure.crypto.UpbitApiClient;
import com.genielee.projectboard.repository.crypto.CryptocurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 암호화폐 가격 정보 관리 서비스
 * 외부 API를 통한 가격 정보 조회 및 데이터베이스 저장/조회 기능 제공
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CryptoPriceService {
    
    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final CoinGeckoApiClient coinGeckoApiClient;
    private final UpbitApiClient upbitApiClient;
    
    @Value("${crypto.scheduler.top-cryptocurrencies.count:100}")
    private int topCryptocurrencyCount;
    
    /**
     * 상위 암호화폐 가격 정보를 조회하여 데이터베이스에 저장합니다
     */
    public Mono<List<CryptocurrencyDto>> updateTopCryptocurrencyPrices() {
        log.info("상위 {} 개 암호화폐 가격 정보 업데이트 시작", topCryptocurrencyCount);
        
        return coinGeckoApiClient.getTopCryptocurrencies(topCryptocurrencyCount, "usd,krw")
                .doOnSuccess(marketDataList -> {
                    log.debug("CoinGecko에서 {} 개 암호화폐 데이터 수신", marketDataList.size());
                    saveOrUpdateCryptocurrencies(marketDataList);
                })
                .map(this::convertToDto)
                .doOnSuccess(result -> log.info("암호화폐 가격 정보 업데이트 완료: {} 개", result.size()))
                .doOnError(error -> log.error("암호화폐 가격 정보 업데이트 실패", error))
                .onErrorReturn(List.of()); // 실패 시 빈 리스트 반환
    }
    
    /**
     * 특정 암호화폐들의 가격 정보를 조회합니다
     */
    public Mono<List<CryptocurrencyDto>> getCryptocurrencyPrices(List<String> coinIds) {
        log.debug("특정 암호화폐 가격 조회: {}", coinIds);
        
        return coinGeckoApiClient.getSimplePrices(coinIds, "usd,krw")
                .map(simplePrices -> {
                    return coinIds.stream()
                            .map(coinId -> {
                                var coinPrice = simplePrices.getCoinPrice(coinId);
                                if (coinPrice != null) {
                                    return CryptocurrencyDto.of(coinId, "", "", 
                                            coinPrice.getUsd(), coinPrice.getKrw());
                                }
                                return null;
                            })
                            .filter(dto -> dto != null)
                            .toList();
                })
                .doOnSuccess(result -> log.debug("특정 암호화폐 가격 조회 완료: {} 개", result.size()))
                .doOnError(error -> log.error("특정 암호화폐 가격 조회 실패", error))
                .onErrorReturn(List.of());
    }
    
    /**
     * 저장된 암호화폐 목록을 조회합니다
     */
    @Transactional(readOnly = true)
    public List<CryptocurrencyDto> getActiveCryptocurrencies() {
        log.debug("활성 암호화폐 목록 조회");
        return cryptocurrencyRepository.findByIsActiveTrueOrderByRankAsc()
                .stream()
                .map(CryptocurrencyDto::from)
                .toList();
    }
    
    /**
     * 페이징된 암호화폐 목록을 조회합니다
     */
    @Transactional(readOnly = true)
    public Page<CryptocurrencyDto> getActiveCryptocurrencies(Pageable pageable) {
        log.debug("페이징된 활성 암호화폐 목록 조회: page={}", pageable.getPageNumber());
        return cryptocurrencyRepository.findByIsActiveTrueOrderByRankAsc(pageable)
                .map(CryptocurrencyDto::from);
    }
    
    /**
     * 상위 N개 암호화폐를 조회합니다
     */
    @Transactional(readOnly = true)
    public List<CryptocurrencyDto> getTopCryptocurrencies() {
        log.debug("상위 10개 암호화폐 조회");
        return cryptocurrencyRepository.findTop10ByIsActiveTrueOrderByRankAsc()
                .stream()
                .map(CryptocurrencyDto::from)
                .toList();
    }
    
    /**
     * 심볼로 암호화폐를 조회합니다
     */
    @Transactional(readOnly = true)
    public Optional<CryptocurrencyDto> getCryptocurrencyBySymbol(String symbol) {
        log.debug("심볼로 암호화폐 조회: {}", symbol);
        return cryptocurrencyRepository.findBySymbol(symbol.toUpperCase())
                .map(CryptocurrencyDto::from);
    }
    
    /**
     * ID로 암호화폐를 조회합니다
     */
    @Transactional(readOnly = true)
    public Optional<CryptocurrencyDto> getCryptocurrencyById(String id) {
        log.debug("ID로 암호화폐 조회: {}", id);
        return cryptocurrencyRepository.findById(id)
                .map(CryptocurrencyDto::from);
    }
    
    /**
     * 암호화폐명 또는 심볼로 검색합니다
     */
    @Transactional(readOnly = true)
    public List<CryptocurrencyDto> searchCryptocurrencies(String keyword) {
        log.debug("암호화폐 검색: {}", keyword);
        return cryptocurrencyRepository.searchByNameOrSymbol(keyword)
                .stream()
                .map(CryptocurrencyDto::from)
                .toList();
    }
    
    /**
     * 페이징된 검색 결과를 반환합니다
     */
    @Transactional(readOnly = true)
    public Page<CryptocurrencyDto> searchCryptocurrencies(String keyword, Pageable pageable) {
        log.debug("페이징된 암호화폐 검색: {} (page={})", keyword, pageable.getPageNumber());
        return cryptocurrencyRepository.searchByNameOrSymbol(keyword, pageable)
                .map(CryptocurrencyDto::from);
    }
    
    /**
     * CoinGecko API 연결 상태를 확인합니다
     */
    public Mono<Boolean> checkApiConnection() {
        log.debug("API 연결 상태 확인");
        return coinGeckoApiClient.ping()
                .doOnSuccess(isConnected -> 
                        log.info("CoinGecko API 연결 상태: {}", isConnected ? "정상" : "비정상"))
                .doOnError(error -> log.error("API 연결 상태 확인 실패", error));
    }
    
    /**
     * 마켓 데이터를 데이터베이스에 저장하거나 업데이트합니다
     */
    private void saveOrUpdateCryptocurrencies(List<CoinGeckoMarketData> marketDataList) {
        marketDataList.forEach(marketData -> {
            try {
                Optional<Cryptocurrency> existingCrypto = cryptocurrencyRepository.findById(marketData.getId());
                
                Cryptocurrency cryptocurrency;
                if (existingCrypto.isPresent()) {
                    // 기존 데이터 업데이트
                    cryptocurrency = existingCrypto.get();
                    updateCryptocurrencyFromMarketData(cryptocurrency, marketData);
                } else {
                    // 새 데이터 생성
                    cryptocurrency = createCryptocurrencyFromMarketData(marketData);
                }
                
                cryptocurrencyRepository.save(cryptocurrency);
                log.debug("암호화폐 정보 저장 완료: {} ({})", 
                        cryptocurrency.getName(), cryptocurrency.getSymbol());
                
            } catch (Exception e) {
                log.error("암호화폐 정보 저장 실패: {} - {}", 
                        marketData.getId(), e.getMessage(), e);
            }
        });
    }
    
    /**
     * 마켓 데이터에서 새로운 암호화폐 엔티티를 생성합니다
     */
    private Cryptocurrency createCryptocurrencyFromMarketData(CoinGeckoMarketData marketData) {
        var cryptocurrency = Cryptocurrency.of(
                marketData.getId(),
                marketData.getSymbol(),
                marketData.getName()
        );
        
        updateCryptocurrencyFromMarketData(cryptocurrency, marketData);
        return cryptocurrency;
    }
    
    /**
     * 마켓 데이터로 암호화폐 정보를 업데이트합니다
     */
    private void updateCryptocurrencyFromMarketData(Cryptocurrency cryptocurrency, CoinGeckoMarketData marketData) {
        // KRW 가격 계산 (USD * 1350 - 대략적인 환율)
        BigDecimal krwPrice = marketData.getCurrentPrice() != null ?
                marketData.getCurrentPrice().multiply(BigDecimal.valueOf(1350)) : null;
        
        cryptocurrency.updatePriceInfo(
                marketData.getCurrentPrice(),
                krwPrice,
                marketData.getMarketCap(),
                marketData.getMarketCapRank(),
                marketData.getPriceChangePercentage24h(),
                marketData.getPriceChange24h(),
                marketData.getHigh24h(),
                marketData.getLow24h(),
                marketData.getTotalVolume()
        );
        
        if (marketData.getImage() != null) {
            cryptocurrency.updateImage(marketData.getImage());
        }
    }
    
    /**
     * 마켓 데이터 리스트를 DTO 리스트로 변환합니다
     */
    private List<CryptocurrencyDto> convertToDto(List<CoinGeckoMarketData> marketDataList) {
        return marketDataList.stream()
                .map(marketData -> {
                    BigDecimal krwPrice = marketData.getCurrentPrice() != null ?
                            marketData.getCurrentPrice().multiply(BigDecimal.valueOf(1350)) : null;
                    
                    return CryptocurrencyDto.of(
                            marketData.getId(),
                            marketData.getSymbol(),
                            marketData.getName(),
                            marketData.getCurrentPrice(),
                            krwPrice
                    );
                })
                .toList();
    }
}