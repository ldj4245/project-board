package com.genielee.projectboard.service.crypto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 암호화폐 가격 정보 스케줄링 서비스
 * 주기적으로 외부 API에서 가격 정보를 조회하여 데이터베이스를 업데이트
 */
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Service
@ConditionalOnProperty(name = "crypto.scheduler.price-update.enabled", havingValue = "true", matchIfMissing = true)
public class CryptoPriceScheduler {
    
    private final CryptoPriceService cryptoPriceService;
    private final CryptoCategoryService cryptoCategoryService;
    
    /**
     * 주기적으로 상위 암호화폐 가격 정보를 업데이트합니다
     * 기본값: 1분마다 실행
     */
    @Async
    @Scheduled(
            fixedDelayString = "${crypto.scheduler.price-update.fixed-delay:60000}",
            initialDelayString = "${crypto.scheduler.price-update.initial-delay:30000}"
    )
    public void updateCryptocurrencyPrices() {
        log.debug("암호화폐 가격 정보 스케줄 업데이트 시작");
        
        try {
            cryptoPriceService.updateTopCryptocurrencyPrices()
                    .subscribe(
                            cryptocurrencies -> {
                                log.info("스케줄된 암호화폐 가격 업데이트 완료: {} 개", cryptocurrencies.size());
                            },
                            error -> {
                                log.error("스케줄된 암호화폐 가격 업데이트 실패", error);
                            }
                    );
        } catch (Exception e) {
            log.error("암호화폐 가격 업데이트 스케줄 실행 중 예외 발생", e);
        }
    }
    
    /**
     * API 연결 상태를 주기적으로 확인합니다
     * 기본값: 5분마다 실행
     */
    @Async
    @Scheduled(fixedDelay = 300000) // 5분마다
    public void checkApiHealthStatus() {
        log.debug("API 연결 상태 확인 스케줄 시작");
        
        try {
            cryptoPriceService.checkApiConnection()
                    .subscribe(
                            isConnected -> {
                                if (isConnected) {
                                    log.info("API 연결 상태 정상");
                                } else {
                                    log.warn("API 연결 상태 비정상 - 백업 API 사용 검토 필요");
                                }
                            },
                            error -> {
                                log.error("API 연결 상태 확인 실패", error);
                            }
                    );
        } catch (Exception e) {
            log.error("API 연결 상태 확인 스케줄 실행 중 예외 발생", e);
        }
    }
    
    /**
     * 애플리케이션 시작 시 기본 카테고리를 초기화합니다
     * 1회성 실행
     */
    @Async
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE) // 시작 후 5초 뒤 1회 실행
    public void initializeDefaultCategories() {
        log.info("기본 카테고리 초기화 스케줄 시작");
        
        try {
            cryptoCategoryService.initializeDefaultCategories();
            log.info("기본 카테고리 초기화 완료");
        } catch (Exception e) {
            log.error("기본 카테고리 초기화 실패", e);
        }
    }
}